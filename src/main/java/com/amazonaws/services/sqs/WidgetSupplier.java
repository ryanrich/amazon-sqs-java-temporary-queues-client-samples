package com.amazonaws.services.sqs;

import java.util.concurrent.ThreadLocalRandom;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.util.SQSMessageConsumer;
import com.amazonaws.services.sqs.util.SQSMessageConsumerBuilder;

public class WidgetSupplier {
	
	public static boolean running = true;
	
	public static void main(String[] args) throws Exception {
		String queueUrl = args[0];
		System.out.println("Starting up supplier using queue: " + queueUrl);
		
		AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
		AmazonSQSResponder responder = AmazonSQSResponderClientBuilder.standard()
		        .withAmazonSQS(sqs)
                .build();

		SQSMessageConsumer consumer = SQSMessageConsumerBuilder.standard()
				.withAmazonSQS(responder.getAmazonSQS())
				.withQueueUrl(queueUrl)
				.withConsumer(message -> {
					int x = ThreadLocalRandom.current().nextInt(10) + 1;
					String responseBody = "Here are " + x + " more widgets. Enjoy!";
					System.out.println("Sending reply: " + responseBody);
					responder.sendResponseMessage(MessageContent.fromMessage(message),
							                      new MessageContent(responseBody));
				}).build();
		consumer.start();
		
		while (running) {};
	}
}
