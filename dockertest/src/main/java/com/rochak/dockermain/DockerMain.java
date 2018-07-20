package com.rochak.dockermain;


import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports.Binding;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.PullImageResultCallback;



public class DockerMain {
	
	private ExposedPort TCP_4444 = ExposedPort.tcp(4444);
	private ExposedPort TCP_5900 = ExposedPort.tcp(5900);
	private int port = 32769;
	private int debugPort = 32768;
	private String hostIp = "0.0.0.0";
	private String containerId;
	private String container_name = "Container_Chrome_10";
	private String image_name = "selenium/standalone-chrome";
	private String debug_image_name = "selenium/standalone-chrome-debug";
	private String image_tag = "latest";
	// Here change image_name to debug_image_name in case of debug build running
	private String image_repo = debug_image_name + ":" + image_tag;
	private static final DockerClient dockerClient = DockerClientBuilder.getInstance().build();

	
	
	public void verifyImage() throws InterruptedException, FileNotFoundException {
		// Checks if image is present else pulls from repository

		List<Image> images = dockerClient.listImagesCmd().withShowAll(true).exec();

		int ctr = 0;
		for (int i = 0; i < images.size(); i++) {
			String[] repos = images.get(i).getRepoTags();
			if (repos[0].equals(image_repo)) {
				System.out.println("Image found with tag: " + repos[0]);
				break;
			}
			ctr++;
		}

		if (ctr == images.size()) {
			System.out.println("Image not found need to pull form repository");
			System.out.println("Exceuting commmand --> docker pull " + image_repo);
			dockerClient.pullImageCmd(image_repo).exec(new PullImageResultCallback()).awaitCompletion(5,
					TimeUnit.MINUTES);

			String[] lastDownloadedImage = dockerClient.listImagesCmd().withShowAll(true).exec().get(0).getRepoTags();

			if (lastDownloadedImage[0].equals(image_repo)) {
				System.out.println("Image Dowloaded !!");
			} else {
				throw new FileNotFoundException("Image not downloaded within mentioned time check on Docker");
			}

		}

	}

	public void startContainer() {

		// Bind server port
		PortBinding server_port = new PortBinding(Binding.bindIpAndPort(hostIp, port), TCP_4444);

		try {
			// Creates container and returns contianerId
			containerId = dockerClient.createContainerCmd(image_name).withPortBindings(server_port)
					.withName(container_name).exec().getId();

			// Start the container
			dockerClient.startContainerCmd(containerId).exec();
			System.out.println("Container Started with ID: " + containerId);
			Thread.sleep(10000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startContainerWithDebugPorts() {

		PortBinding server_port = new PortBinding(Binding.bindIpAndPort(hostIp, port), TCP_4444);
		PortBinding debug_port = new PortBinding(Binding.bindIpAndPort(hostIp, debugPort), TCP_5900);

		try {
			// Creates container and returns cotnianerId
			containerId = dockerClient.createContainerCmd(debug_image_name).withPortBindings(server_port, debug_port)
					.withName(container_name + "-Debug").exec().getId();

			// Start the container
			dockerClient.startContainerCmd(containerId).exec();
			System.out.println("Container Started with ID: " + containerId);
			Thread.sleep(10000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void stopContainer() {

		try {

			dockerClient.stopContainerCmd(containerId).withTimeout(3).exec();
			InspectContainerResponse inspectContainerResponse = dockerClient.inspectContainerCmd(containerId).exec();

//			Boolean bb = inspectContainerResponse.getState().getRunning();
//			Boolean mm = false;
//			assertEquals(bb, mm);
			System.out.println("Container Stopped with exit code:" + inspectContainerResponse.getState().getExitCode());

			// To remove the container if required.
			dockerClient.removeContainerCmd(containerId).exec();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
