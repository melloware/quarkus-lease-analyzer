# Quarkus Lease Analyzer

This example demonstrates how to analyze a lease PDF document using Google AI Gemini
and get a structured output metadata about the lease.

## Project Overview

This application is a web-based tool that extracts key information from lease agreements using Google's Gemini AI model. It provides a simple interface where users can:

1. Upload PDF lease documents
2. Get structured analysis of lease terms including:
   - Agreement date
   - Lease term start and end dates 
   - Development term end date
   - Landlord and tenant names
   - Property size in acres

### Technical Details

The application is built using:

- Quarkus - A Kubernetes-native Java framework
- LangChain4j - Java bindings for LangChain to interact with LLMs
- Google Gemini AI - For PDF document analysis and information extraction
- RESTEasy - For handling multipart file uploads
- HTML/JavaScript frontend - Simple UI for file upload and results display

The backend processes the PDF through these steps:
1. Accepts PDF upload via multipart form data
2. Converts PDF content to base64 encoding
3. Sends to Gemini AI with a structured JSON schema for response formatting
4. Returns parsed lease information in a standardized format
5. Displays results in a tabular format on the web interface

### Architecture

The main components are:
- `LeaseAnalyzerResource` - REST endpoint for PDF analysis
- `LeaseReport` - Data structure for lease information
- Web interface for file upload and results display


## Running the example

A prerequisite to running this example is to provide your Google Gemini API key.
You can get one for free, see more details here: https://ai.google.dev/gemini-api/docs/api-key

```
export GOOGLE_AI_GEMINI_API_KEY=<your-google-ai-gemini-api-key>
```

Then, simply run the project in Dev mode:

```
mvn quarkus:dev
```

## Using the example

Open the application at http://localhost:8080 and click `Upload` then `Analyze`.


![Lease Analyzer Screenshot](lease-analyzer.png)


