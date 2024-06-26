package tlschannel.async;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tlschannel.helpers.SocketPairFactory.NULL_CIPHER;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import tlschannel.helpers.AsyncLoops;
import tlschannel.helpers.SocketGroups.AsyncSocketPair;
import tlschannel.helpers.SocketPairFactory;
import tlschannel.helpers.SslContextFactory;

@TestInstance(Lifecycle.PER_CLASS)
public class AsyncTest implements AsyncTestBase {

    private final SslContextFactory sslContextFactory = new SslContextFactory();
    private final SocketPairFactory factory = new SocketPairFactory(sslContextFactory.defaultContext());
    private final int socketPairCount = 50;

    // real engine - run tasks
    @Test
    public void testRunTasks() throws Throwable {
        System.out.println("testRunTasks():");
        AsynchronousTlsChannelGroup channelGroup = new AsynchronousTlsChannelGroup();
        int dataSize = 5 * 1024 * 1024;
        System.out.printf("data size: %d\n", dataSize);
        List<AsyncSocketPair> socketPairs =
                factory.asyncN(Optional.empty(), channelGroup, socketPairCount, true, false);
        AsyncLoops.Report report = AsyncLoops.loop(socketPairs, dataSize);

        shutdownChannelGroup(channelGroup);
        assertChannelGroupConsistency(channelGroup);
        assertEquals(0, channelGroup.getFailedReadCount());
        assertEquals(0, channelGroup.getFailedWriteCount());

        report.print();
        printChannelGroupStatus(channelGroup);
    }

    // real engine - do not run tasks
    @Test
    public void testNotRunTasks() throws Throwable {
        System.out.println("testNotRunTasks():");
        AsynchronousTlsChannelGroup channelGroup = new AsynchronousTlsChannelGroup();
        int dataSize = 2 * 1024 * 1024;
        System.out.printf("data size: %d\n", dataSize);
        List<AsyncSocketPair> socketPairs =
                factory.asyncN(Optional.empty(), channelGroup, socketPairCount, false, false);
        AsyncLoops.Report report = AsyncLoops.loop(socketPairs, dataSize);

        shutdownChannelGroup(channelGroup);
        assertChannelGroupConsistency(channelGroup);

        assertEquals(0, channelGroup.getFailedReadCount());
        assertEquals(0, channelGroup.getFailedWriteCount());
        assertEquals(0, channelGroup.getCancelledReadCount());
        assertEquals(0, channelGroup.getCancelledWriteCount());

        report.print();
        printChannelGroupStatus(channelGroup);
    }

    // null engine
    @Test
    public void testNullEngine() throws Throwable {
        System.out.println("testNullEngine():");
        AsynchronousTlsChannelGroup channelGroup = new AsynchronousTlsChannelGroup();
        int dataSize = 12 * 1024 * 1024;
        System.out.printf("data size: %d\n", dataSize);
        List<AsyncSocketPair> socketPairs =
                factory.asyncN(Optional.of(NULL_CIPHER), channelGroup, socketPairCount, true, false);
        AsyncLoops.Report report = AsyncLoops.loop(socketPairs, dataSize);

        shutdownChannelGroup(channelGroup);
        assertChannelGroupConsistency(channelGroup);

        assertEquals(0, channelGroup.getFailedReadCount());
        assertEquals(0, channelGroup.getFailedWriteCount());
        assertEquals(0, channelGroup.getCancelledReadCount());
        assertEquals(0, channelGroup.getCancelledWriteCount());

        report.print();
        printChannelGroupStatus(channelGroup);
    }
}
