package com.mayuran19.maven.plugin.LocalToRemoteRepo;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by smayuran on 10/5/2017.
 */
public class FileCopy {
    private String userName;
    private String password;
    private String remoteRepoPath;
    private String localRepoPath;

    public static void main(String args[]) {
        /*copyFiles(Paths.get("C:\\Users\\smayuran.SITBCS\\.m2\\repository\\org\\springframework\\integration\\spring-integration-core\\4.3.4.RELEASE\\spring-integration-core-4.3.4.RELEASE.jar")
                , "smb://192.168.9.196/Softwares/Mayuran/spring-integration-core-4.3.4.RELEASE.jar", "G3App", "P@ssword$1"); */
    }

    public void copyFiles(Path source, String destination) {
        try {
            NtlmPasswordAuthentication ntlmPasswordAuthentication = new NtlmPasswordAuthentication(null, this.getUserName(), this.getPassword());
            SmbFile smbFile = new SmbFile(destination.toString(), ntlmPasswordAuthentication);
            try (SmbFileOutputStream smbFileOutputStream = new SmbFileOutputStream(smbFile)) {
                Files.copy(source, smbFileOutputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void copyFiles(Artifact artifact, Log log) {
        log.info("localRepoPath: " + localRepoPath);
        log.info("groupID:" + artifact.getGroupId());
        Path path = Paths.get(localRepoPath, String.join("/", artifact.getGroupId().split("\\.")), artifact.getArtifactId(), artifact.getVersion());
        log.info("path: " + path.toString());
        for (File file : path.toFile().listFiles()) {
            String remoteFilePath = String.join("/", remoteRepoPath, String.join("/", artifact.getGroupId().split("\\.")), artifact.getArtifactId(), artifact.getVersion());
            String remoteFile = String.join("/", remoteFilePath, file.getName());
            if (!isFileExistsInRemote(remoteFile)) {
                log.info("File doesn't exists: " + remoteFile);
                createFolderStructure(artifact, remoteRepoPath, log);
                copyFiles(Paths.get(path.toString(), file.getName()), remoteFile);
            } else {
                log.info("File exists: " + remoteFile);
            }
        }
    }

    public boolean isFileExistsInRemote(String path) {
        boolean isExists = false;
        NtlmPasswordAuthentication ntlmPasswordAuthentication = new NtlmPasswordAuthentication(null, this.getUserName(), this.getPassword());
        try {
            SmbFile smbFile = new SmbFile(path, ntlmPasswordAuthentication);
            if (smbFile.exists()) {
                isExists = true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SmbException e) {
            e.printStackTrace();
        }

        return isExists;
    }

    public void createFolderStructure(Artifact artifact, String remoteFilePath, Log log){
        String basePath = remoteFilePath;
        try {
            String[] groupId = artifact.getGroupId().split("\\.");
            NtlmPasswordAuthentication ntlmPasswordAuthentication = new NtlmPasswordAuthentication(null, this.getUserName(), this.getPassword());
            for(String s : groupId){
                String g = String.join("/", basePath, s);
                log.info("g:" +g);
                SmbFile smbFile = new SmbFile(g, ntlmPasswordAuthentication);
                if(!smbFile.exists()){
                    smbFile.mkdir();
                }
                basePath = basePath + "/" + s;
            }
            SmbFile artifactId = new SmbFile(String.join("/", basePath, artifact.getArtifactId()), ntlmPasswordAuthentication);
            if(!artifactId.exists()){
                artifactId.mkdir();
            }
            basePath = basePath + "/" + artifact.getArtifactId();
            SmbFile version = new SmbFile(String.join("/", basePath, artifact.getVersion()), ntlmPasswordAuthentication);
            if(!version.exists()){
                version.mkdir();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SmbException e) {
            e.printStackTrace();
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRemoteRepoPath() {
        return remoteRepoPath;
    }

    public void setRemoteRepoPath(String remoteRepoPath) {
        this.remoteRepoPath = remoteRepoPath;
    }

    public String getLocalRepoPath() {
        return localRepoPath;
    }

    public void setLocalRepoPath(String localRepoPath) {
        this.localRepoPath = localRepoPath;
    }
}
