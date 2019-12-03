package segmentedfilesystem;

public class Main {
    
    public static void main(String[] args) {
        
    }

    class DataPacket {
        byte statusByte;
        byte fileId;
        byte[] packetNumber = new byte[2];
        byte[] data = new byte[1024];
        boolean header = false;
        public DataPacket(byte[] bytes) {
            this.statusByte = bytes[0];
            this.fileId = bytes[1];
            if(this.statusByte % 2 == 0) {
                this.header = true;
            }
            if(this.header) {
                System.arraycopy(bytes, 2, this.data,0,1024);
            }
            else {
                this.packetNumber[0] = bytes[2];
                this.packetNumber[1] = bytes[3];
                System.arraycopy(bytes, 4, this.data,0,1024);
            }
        }
    }

}
