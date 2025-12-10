package de.weiner.dataengineering.streamingpipeline;

import de.weiner.dataengineering.streamingpipeline.kafka.AirQualityProducer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StreamingPipelineApplication implements CommandLineRunner {

	private final AirQualityProducer producer;

	public StreamingPipelineApplication(AirQualityProducer producer) {
		this.producer = producer;
	}

	public static void main(String[] args) {
		SpringApplication.run(StreamingPipelineApplication.class, args);
	}

	public void runTest(String... args) throws Exception {
		String payload = "hello-kafka-" + System.currentTimeMillis();
		producer.sendTestMessage(payload);
	}

	@Override
	public void run(String... args) throws Exception {
		producer.streamFromCsv();
	}
}
