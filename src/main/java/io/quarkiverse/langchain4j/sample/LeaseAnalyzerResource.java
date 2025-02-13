package io.quarkiverse.langchain4j.sample;

import static dev.langchain4j.model.chat.request.ResponseFormatType.JSON;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import dev.langchain4j.data.message.PdfFileContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.output.JsonSchemas;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.jbosslog.JBossLog;

/**
 * REST resource for analyzing lease documents using Google Gemini AI.
 * Provides endpoints for uploading and analyzing PDF lease agreements.
 */
@Path("/analyze-lease")
@JBossLog
public class LeaseAnalyzerResource {

    /** The chat language model used for document analysis */
    @Inject
    ChatLanguageModel model;

    /**
     * Uploads and analyzes a PDF lease document.
     * 
     * @param fileUploadRequest The multipart form request containing the PDF file
     * @return JSON string containing the analyzed lease information
     */
    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/upload")
    public String upload(@MultipartForm @Valid final LeaseUploadRequest fileUploadRequest) {
        final String fileName = fileUploadRequest.getFileName();
        log.infof("Uploading file: %s", fileName);
        final InputStream data = fileUploadRequest.getData();

        ByteArrayInputStream inputStream;
        try {
            // Convert input stream to byte array for processing
            inputStream = new ByteArrayInputStream(
                    IOUtils.toBufferedInputStream(data).readAllBytes());

            // Encode PDF content to base64 for transmission
            String documentEncoded = Base64.getEncoder().encodeToString(inputStream.readAllBytes());

            // Create user message with PDF content for analysis
            UserMessage userMessage = UserMessage.from(
                    TextContent.from("Analyze the given document"),
                    PdfFileContent.from(documentEncoded, "application/pdf"));

            // Build chat request with JSON response format
            ChatRequest chatRequest = ChatRequest.builder()
                    .messages(userMessage)
                    .parameters(ChatRequestParameters.builder()
                            .responseFormat(responseFormatFrom(LeaseReport.class))
                            .build())
                    .build();

            log.info("Google Gemini analyzing....");
            ChatResponse chatResponse = model.chat(chatRequest);
            String response = chatResponse.aiMessage().text();
            log.infof("Google Gemini analyzed: %s", response);

            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a JSON response format for the specified class.
     * 
     * @param clazz The class to create JSON schema from
     * @return ResponseFormat configured for JSON output
     */
    private static ResponseFormat responseFormatFrom(Class<?> clazz) {
        return ResponseFormat.builder()
                .type(JSON)
                .jsonSchema(JsonSchemas.jsonSchemaFrom(clazz).get())
                .build();
    }
}
