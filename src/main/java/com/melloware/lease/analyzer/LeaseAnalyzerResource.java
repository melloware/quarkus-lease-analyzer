package com.melloware.lease.analyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import dev.langchain4j.data.message.PdfFileContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ResponseFormatType;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.output.JsonSchemas;
import jakarta.inject.Inject;
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
    @Operation(summary = "Upload and analyze a lease document", description = "Uploads a PDF lease document and analyzes it using Google Gemini AI to extract key information")
    @RequestBody(required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA, schema = @Schema(implementation = FileUpload.class)))
    @APIResponse(responseCode = "200", description = "Successfully analyzed lease document", content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(implementation = LeaseReport.class)))
    public String upload(@RestForm("file") FileUpload fileUploadRequest) {
        final String fileName = fileUploadRequest.fileName();
        log.infof("Uploading file: %s", fileName);

        try {
            // Convert input stream to byte array for processing
            byte[] fileBytes = Files.readAllBytes(fileUploadRequest.filePath());

            // Encode PDF content to base64 for transmission
            String documentEncoded = Base64.getEncoder().encodeToString(fileBytes);

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
            long startTime = System.currentTimeMillis();
            ChatResponse chatResponse = model.chat(chatRequest);
            long endTime = System.currentTimeMillis();
            String response = chatResponse.aiMessage().text();
            log.infof("Google Gemini analyzed in %d ms: %s", (endTime - startTime), response);

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
                .type(ResponseFormatType.JSON)
                .jsonSchema(JsonSchemas.jsonSchemaFrom(clazz).get())
                .build();
    }
}