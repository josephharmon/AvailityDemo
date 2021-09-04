package hl7;

import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Scanner;
import java.net.Socket;



public class ReadHL7File {

  public static void main(String[] args) {
    try {
      getFilesFromDirectory(null);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

private static final char startOfMessage = '\u000b';
private static final char endOfLine      = 13;
private static final char endOfMessage   = '\u001c';

public static void readHl7File(String fileName) {
      try {
        System.out.println("Attempting to read file...");
        File hl7filetoRead = new File(fileName);
        Scanner hl7Reader = new Scanner(hl7filetoRead);

        StringBuffer hl7Data = new StringBuffer();

        hl7Data.append(startOfMessage);

        while (hl7Reader.hasNextLine()) {
           hl7Data.append( hl7Reader.nextLine());
           hl7Data.append(endOfLine);
        }
        hl7Reader.close();
        hl7Data.append(endOfMessage);
        hl7Data.append(endOfLine);

        System.out.println("Created Message:");
        System.out.println(hl7Data);
        try{
            SendHL7Message( hl7Data );

        }catch(IOException e){
            System.out.println("Unable to send message");
            e.printStackTrace();
        }
      } catch (FileNotFoundException e) {
        System.out.println("File Not Found.");
        e.printStackTrace();
      }
    }

    public static void SendHL7Message(StringBuffer hl7Data) throws IOException {

        Socket socket = new Socket("localhost", 1160);

        InputStream in = socket.getInputStream();
	      OutputStream out = socket.getOutputStream();

        System.out.println("Sending message...");
        
        out.write(hl7Data.toString().getBytes());
        
        System.out.println("Message Sent!");
        
        byte[] byteBuffer = new byte[200];
        in.read(byteBuffer);

        System.out.println("Received ACK from Server: " + new String(byteBuffer));

        socket.close();
    }

    public static void getFilesFromDirectory(String args[]) throws IOException, InterruptedException {
      File directoryPath = new File("C:\\HL7Files\\ToSend");
      File filesList[] = directoryPath.listFiles();
  
      if (filesList.length == 0 ){
        System.out.println("Path is empty.  Waiting 5 seconds...");
        Thread.sleep(5000);
      }

      else{
      for(File file : filesList) {
        readHl7File(file.getAbsolutePath());
        moveFile(file);
        Thread.sleep(5000);
      }
    }
      getFilesFromDirectory(null);
  }

  public static void moveFile(File fileToMove) throws IOException{
    String archivePath = "C:\\HL7Files\\ArchivedMessages\\";
    File archiveFile = new File(archivePath + fileToMove.getName());
  
    if (archiveFile.exists()){
      archiveFile.delete();
    }

    FileWriter NewArchiveFile = new FileWriter(archivePath + fileToMove.getName());
    File originalFile = new File(fileToMove.getAbsolutePath());
    Scanner originalFileReader = new Scanner(originalFile);
    
    while (originalFileReader.hasNextLine()) {
          NewArchiveFile.write(originalFileReader.nextLine());
          NewArchiveFile.write(endOfLine);
    }
    
    NewArchiveFile.close();
    originalFileReader.close();
    originalFile.delete();
  }
}