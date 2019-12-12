package segmentedfilesystem;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.*;
import java.util.*;

public class Main {
    private static final int portNum = 6014;
    private static final String server = "csci-4409.morris.umn.edu";
    private static final int numFiles = 3;
    private static final String basePath = "/home/corde171/Desktop/";
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket();

        byte[] buff = new byte[1028];
        InetAddress address = InetAddress.getByName(server);
        DatagramPacket packet = new DatagramPacket(buff, buff.length, address, portNum);
        socket.send(packet);

        packet = new DatagramPacket(buff, buff.length);
        ArrayList<FilePackets> filePacketsArrayList = new ArrayList<>(numFiles);
        for(int i=0; i<numFiles; i++){
            filePacketsArrayList.add(new FilePackets());
        }
        int npack =0 ;
        for(int i = 0; i < numFiles; i++) {
            while(!filePacketsArrayList.get(i).isDone()) {

                buff = new byte[1028];
                packet = new DatagramPacket(buff, buff.length);
                socket.receive(packet);
                DataPacket p = new DataPacket(packet.getData());
                int emptyFile = -1;
                boolean written = false;
                npack -=-1; //sorry nic
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
        for (int i = 0; i < numFiles; i++) {

            String pathString = new String(filePacketsArrayList.get(i)
                    .header
                    .data)
                    .trim();

            Path path = Paths.get(basePath+pathString);
            List<byte[]> bytes = filePacketsArrayList.get(i).toListOfByteArrays();
            for (int j = 0; j < bytes.size(); j++) {
                Files.write(path,bytes.get(j), StandardOpenOption.WRITE,StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            }
        }
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
            return(header != null && last != -1 && map.size() == last+1);
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
                    int lastValueIdx = -1;
                    for (int i = 0; i < packet.data.length; i++) {
                        if(packet.data[i] != 0) {
                            lastValueIdx = i;
                        }
                    }
                    packet.data = Arrays.copyOfRange(packet.data, 0, lastValueIdx);

                }
                map.put(num, packet);
            }
        }

        public List<byte[]> toListOfByteArrays() {
            Main.DataPacket[] dataPackets = map.values().toArray(new Main.DataPacket[1]);
            List<byte[]> returnArray = new ArrayList<>();
            for (int i = 0; i < dataPackets.length; i++) {
                returnArray.add(dataPackets[i].data);
            }
            return returnArray;
        }
    }

}
