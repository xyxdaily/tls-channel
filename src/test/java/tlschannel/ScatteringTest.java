package tlschannel;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import tlschannel.helpers.Loops;
import tlschannel.helpers.SocketGroups.SocketPair;
import tlschannel.helpers.SocketPairFactory;
import tlschannel.helpers.SslContextFactory;

@TestInstance(Lifecycle.PER_CLASS)
public class ScatteringTest {

    private final SslContextFactory sslContextFactory = new SslContextFactory();
    private final SocketPairFactory factory = new SocketPairFactory(sslContextFactory.defaultContext());

    private static final int dataSize = 150 * 1000;

    @Test
    public void testHalfDuplex() {
        SocketPair socketPair = factory.nioNio(Optional.empty(), Optional.empty(), true, false, Optional.empty());
        Loops.halfDuplex(socketPair, dataSize, true, false);
    }
}
