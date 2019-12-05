package segmentedfilesystem;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * This is just a stub test file. You should rename it to
 * something meaningful in your context and populate it with
 * useful tests.
 */
public class ClientTest {

    @Test
    public void dataPacketHeaderTest() {
        byte[] bytes = new byte[1024];
        byte[] temp = {16,0,55,110,67,68,59};
        System.arraycopy(temp, 0, bytes,0,7);
        Main.DataPacket packet = new Main.DataPacket(bytes);
        assertTrue(packet.header);
    }

    @Test
    public void dataPacketLastTest() {
        byte[] bytes = new byte[1024];
        byte[] temp = {15,0,55,110,67,68,59};
        System.arraycopy(temp, 0, bytes,0,7);
        Main.DataPacket packet = new Main.DataPacket(bytes);
        assertFalse(packet.header);
        assertTrue(packet.isLast);
    }
    @Test
    public void dataPacketDataTest() {
        byte[] bytes = new byte[1024];
        byte[] temp = {13,0,55,110,67,68,59};
        System.arraycopy(temp, 0, bytes,0,7);
        Main.DataPacket packet = new Main.DataPacket(bytes);
        assertFalse(packet.header);
        assertFalse(packet.isLast);
    }

    @Test
    public void dataPacketsFullTest() {
        byte[] bytes = new byte[1024];
        byte[] temp = {3,0,0,0,67,68,59};
        System.arraycopy(temp, 0,bytes,0,7);
        Main.DataPacket packet = new Main.DataPacket(bytes);
        bytes[0] = 0;
        Main.FilePackets file = new Main.FilePackets(new Main.DataPacket(bytes));
        assertFalse(file.isDone());
        file.addPacket(packet);
        assertTrue(file.isDone());
        file.addPacket(new Main.DataPacket(bytes));
        assertTrue(file.isDone());
    }

}
