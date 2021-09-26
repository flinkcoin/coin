package com.flick.node;

import com.beust.jcommander.JCommander;
import com.flick.crypto.KeyGenerator;
import com.flick.crypto.KeyPair;
import com.flick.helper.helpers.Base32Helper;
import com.flick.helper.helpers.RandomHelper;
import com.flick.helper.helpers.UUIDHelper;
import com.flick.node.api.ApiServer;
import com.flick.node.communication.ClientInitializer;
import com.flick.node.configuration.CommandLineArguments;
import com.flick.node.communication.ServerInitializer;
import com.flick.node.configuration.bootstrap.Bootstrap;
import com.flick.node.configuration.CommandLineArguments.DaemonCommand;
import com.flick.node.configuration.CommandLineArguments.GenerateCommand;
import com.flick.node.jobs.JobsInit;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.IOException;
import org.quartz.SchedulerException;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private final CommandLineArguments commandLineArguments;
    private final Injector injector;
    private final JobsInit jobsInit;
    private final ServerInitializer server;
    private final ClientInitializer client;
    private final ApiServer apiServer;

    public Main(CommandLineArguments commandLineArguments, Injector injector, JobsInit jobsInit, ServerInitializer server, ClientInitializer client, ApiServer apiServer) {
        this.commandLineArguments = commandLineArguments;
        this.injector = injector;
        this.jobsInit = jobsInit;
        this.server = server;
        this.client = client;
        this.apiServer = apiServer;
    }

    public static void main(String[] args) throws RocksDBException, SchedulerException, InterruptedException, Exception {
        CommandLineArguments commandLineArguments = new CommandLineArguments();
        GenerateCommand generateCommand = new GenerateCommand();
        DaemonCommand daemonCommand = new DaemonCommand();

        LOGGER.debug("Node Id: {}", 1);

        JCommander jc = JCommander.newBuilder()
                .addObject(commandLineArguments)
                .addCommand(generateCommand)
                .addCommand(daemonCommand)
                .programName("Flick")
                .build();

        jc.parse(args);
        if (commandLineArguments.help) {
            jc.usage();
            return;
        }
        String command = jc.getParsedCommand();
        if (command == null) {
            jc.usage();
            return;
        }
        switch (command) {
            case "generate":
                generate(commandLineArguments);
                break;
            case "daemon":
                initDaemon(commandLineArguments);
                break;

            default:
                jc.usage();
        }
    }

    private static void initDaemon(CommandLineArguments cla) throws Exception, InterruptedException, RocksDBException, SchedulerException {
        Injector injector = Guice.createInjector(new MainModule());
        JobsInit jobsInit = injector.getInstance(JobsInit.class);
        ServerInitializer server = injector.getInstance(ServerInitializer.class);
        ClientInitializer client = injector.getInstance(ClientInitializer.class);
        ApiServer apiServer = injector.getInstance(ApiServer.class);

        Bootstrap bootstrap = injector.getInstance(Bootstrap.class);
        bootstrap.init();
        Main main = new Main(cla, injector, jobsInit, server, client, apiServer);
        main.init();
        //main.stop();
    }

    private static void generate(CommandLineArguments cla) {

        byte[] asBytes = UUIDHelper.asBytes();

        LOGGER.debug("Node Id: {}", Base32Helper.encode(asBytes));

        byte[] seed = new byte[32];
        RandomHelper.get().nextBytes(seed);

        LOGGER.debug("Node Key: {}", Base32Helper.encode(seed));

        KeyPair keyPair = KeyGenerator.getKeyPairFromSeed(seed);

        byte[] publicKey = keyPair.getPublicKey().getPublicKey();

        LOGGER.debug("Node Public Key: {}", Base32Helper.encode(publicKey));

    }

    public void init() throws SchedulerException, InterruptedException, RocksDBException, IOException {
        start();
        shutdownHook();
    }

    public void shutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            stop();
        }));
    }

    private void start() throws SchedulerException, RocksDBException, InterruptedException, IOException {
        server.init();
        client.init();
        apiServer.init();
        jobsInit.start();
    }

    private void stop() {
        try (server; client; apiServer) {
            jobsInit.stop();
        } catch (Exception ex) {
            LOGGER.error("Not stopping cleanly", ex);
        }

    }
}