package com.mayuran19.maven.plugin.LocalToRemoteRepo;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;

/**
 * Created by smayuran on 10/2/2017.
 */
@Mojo(name = "localToRemoteRepo", defaultPhase = LifecyclePhase.PACKAGE, requiresOnline = false,
        requiresProject = true, threadSafe = false, requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class LocalToRemoteRepoMojo extends AbstractMojo {
    @Parameter(property = "localRepoDir", required = true)
    protected File localRepoDir;
    @Parameter(property = "remoteRepoDir", required = true)
    protected String remoteRepoDir;
    @Parameter(defaultValue = "${project}", readonly = true)
    protected MavenProject mavenProject;
    @Parameter(defaultValue = "${localRepository}", readonly = false)
    protected ArtifactRepository local;
    @Parameter(property = "username", required = true)
    protected String username;
    @Parameter(property = "password", required = true)
    protected String password;

    public void execute()
            throws MojoExecutionException, MojoFailureException {
        FileCopy fileCopy = new FileCopy();
        fileCopy.setLocalRepoPath(localRepoDir.getAbsolutePath());
        fileCopy.setRemoteRepoPath(remoteRepoDir);
        fileCopy.setUserName(username);
        fileCopy.setPassword(password);

        mavenProject.getArtifacts().forEach(a -> {
            getLog().info(a.getGroupId() + ":" + a.getArtifactId() + ":" + a.getVersion());
            fileCopy.copyFiles(a, getLog());
        });

        getLog().info("local repo:" + local);
    }
}
