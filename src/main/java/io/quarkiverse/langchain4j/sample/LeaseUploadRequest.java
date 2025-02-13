package io.quarkiverse.langchain4j.sample;

import java.io.InputStream;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;
import lombok.Data;

/**
 * Request object for uploading lease documents.
 * This class handles multipart form data for file uploads.
 */
@Data
public class LeaseUploadRequest {

    /**
     * The input stream containing the lease document file data.
     * Annotated for multipart form handling with octet stream content type.
     */
    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM) 
    @NotNull
    private InputStream data;

    /**
     * The name of the uploaded lease document file.
     * Annotated for multipart form handling with plain text content type.
     */
    @FormParam("fileName")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank
    private String fileName;

}