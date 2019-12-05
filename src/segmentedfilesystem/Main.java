package segmentedfilesystem;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Main {
    private static final int portNum = 6014;
    private static final String server = "csci-4409.morris.umn.edu";
    private static final int numFiles = 3;
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket();

        byte[] buff = new byte[1028];
        InetAddress address = InetAddress.getByName(server);
        DatagramPacket packet = new DatagramPacket(buff, buff.length, address, portNum);
        socket.send(packet);

        packet = new DatagramPacket(buff, buff.length);
        boolean done = false;
        ArrayList<FilePackets> filePacketsArrayList = new ArrayList<>(numFiles);
        for(int i=0; i<numFiles; i++){
            filePacketsArrayList.add(new FilePackets());
        }
        int npack =0 ;
        for(int i = 0; i < numFiles; i++) {
            while(!filePacketsArrayList.get(i).isDone()) {
                socket.receive(packet);
                //System.out.println(packet.getData()[10]);
                DataPacket p = new DataPacket(packet.getData());
                byte id = p.fileId;
                int emptyFile = -1;
                boolean written = false;
                npack -=-1;
                System.out.println(npack);
                for(int j=0; j<numFiles;j++){
                    byte fid = filePacketsArrayList.get(j).getId();
                    if(fid == p.fileId){
                        written = true;
                        filePacketsArrayList.get(j).addPacket(p);
                        break;
                    }else if(fid == -1){
                        emptyFile = j;
                    }
                }
                if(!written){
                    filePacketsArrayList.get(emptyFile).addPacket(p);
                }

            }


        }


        socket.close();
    }

    static class DataPacket {
        byte statusByte;
        byte fileId;
        byte[] packetNumber = new byte[2];
        byte[] data;
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
                this.data = Arrays.copyOfRange(bytes, 2, bytes.length);
            }
            else {
                this.packetNumber[0] = bytes[2];
                this.packetNumber[1] = bytes[3];
                this.data = Arrays.copyOfRange(bytes, 4, bytes.length);
            }
        }
    }

    static class FilePackets {
        private DataPacket header;
        private HashMap<Integer, DataPacket> map;
        private int last = -1;

        public boolean isEmpty(){
            return(this.map.size() == 0 && this.header == null);
        }
        public boolean isDone() {
            return(last != -1 && map.size() == last+1);
        }

        public FilePackets() {
            this.map = new HashMap<>();
        }
        public byte getId(){
            if(this.header != null){
                return header.fileId;
            }else if (map.size() != 0){
                return map.values().iterator().next().fileId;
            }else{
                return -1;
            }
        }
        public void addPacket(DataPacket packet) {
            if(isDone()) {
                return;
            }
            if(packet.header){
                if(header != null) {
                    return;
                }
                this.header = packet;
            }else{
                int num = new BigInteger(packet.packetNumber).intValue();
                if(packet.isLast){
                    last = num;
                }
                map.put(num, packet);
            }
        }
    }

}
