package segmentedfilesystem;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    private final int portNum = 6014;
    private final String server = "csci-4409.morris.umn.edu";
    public static void main(String[] args) {
        
    }

    static class DataPacket {
        byte statusByte;
        byte fileId;
        byte[] packetNumber = new byte[2];
        byte[] data = new byte[1024];
        boolean header = false;
        boolean isLast = false;
        public DataPacket(byte[] bytes) {
            this.statusByte = bytes[0];
            if(this.statusByte % 4 == 3) {
                isLast = true;
            }
            this.fileId = bytes[1];
            if(this.statusByte % 2 == 0) {
                this.header = true;
            }
            if(this.header) {
                System.arraycopy(bytes, 2, this.data,0,6);
            }
            else {
                this.packetNumber[0] = bytes[2];
                this.packetNumber[1] = bytes[3];
                System.arraycopy(bytes, 4, this.data,0,6);
            }
        }
    }

    static class FilePackets {
        private ArrayList<DataPacket> packets;
        private DataPacket header;
        private HashMap<Integer, DataPacket> map;
        public FilePackets(DataPacket header) {
            this.header = header;
        }
        public void addPacket(DataPacket packet) {
            this.packets.add(packet);
        }
    }

}
