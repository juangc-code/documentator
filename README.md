# Documentator

An AI-powered Spring Boot application that automatically generates documentation for code repositories, including README files and architecture diagrams.

## Features

- **Automatic README Generation**: Analyzes repositories and creates comprehensive README.md files
- **Architecture Diagrams**: Generates visual diagrams using Excalidraw to illustrate system architecture and data flows
- **Multi-AI Support**: Integrates with both OpenAI (Kimi) and Google Gemini for intelligent analysis
- **Git Integration**: Automatically clones repositories, creates branches, commits, and pushes documentation changes
- **MCP Client**: Connects to external MCP servers (like Excalidraw) for extended functionality

## Installation

### Prerequisites

- Java 21
- Maven 3.9+
- Docker (optional, for containerized deployment)

### Local Setup

```bash
# Clone the repository
git clone https://github.com/juangc-code/documentator.git
cd documentator

# Build the application
./mvnw clean package

# Or using Maven directly
mvn clean package
```

### Docker Deployment

```bash
# Build and run with Docker Compose
docker-compose up -d

# Or build the Docker image manually
docker build -t documentator:latest .
docker run -p 8080:8080 \
  -e KIMI_API_KEY=your_key \
  -e GH_TOKEN=your_token \
  -e EXCALIDRAW_MCP_URL=http://localhost:8000 \
  documentator:latest
```

## Configuration

Create a `.env` file in the project root with the following variables:

```env
# Required: API key for Kimi (OpenAI-compatible)
KIMI_API_KEY=your_kimi_api_key

# Required: GitHub personal access token
GH_TOKEN=your_github_token

# Required: URL to Excalidraw MCP server
EXCALIDRAW_MCP_URL=http://localhost:8000

# Optional: Gemini API key (alternative AI provider)
GEMINI_API_KEY=your_gemini_api_key
```

## Usage

### Generate README for a Repository

```bash
curl -X POST http://localhost:8080/documentator \
  -H "Content-Type: application/json" \
  -d '{"url": "https://github.com/username/repository.git"}'
```

### Generate Architecture Diagrams

```bash
curl -X POST http://localhost:8080/ \
  -H "Content-Type: application/json" \
  -d '{"url": "https://github.com/username/repository.git"}'
```

The application will:
1. Clone the target repository
2. Analyze the codebase
3. Generate documentation (README or diagrams)
4. Create a new branch (`readme-update` or `diagram-update`)
5. Commit and push the changes

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

## Technologies Used

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-6DB33F?style=flat&logo=spring-boot&logoColor=white)
![Spring AI](https://img.shields.io/badge/Spring%20AI-2.0.0--M4-6DB33F?style=flat&logo=spring&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=flat&logo=apache-maven&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)
![Resilience4j](https://img.shields.io/badge/Resilience4j-2.4.0-000000?style=flat)

### AI Providers
- **Kimi (Moonshot AI)** - Primary LLM for code analysis
- **Google Gemini** - Alternative AI provider

### Key Dependencies
- Spring Boot Web & Actuator
- Spring AI (MCP Client, OpenAI, Google GenAI)
- Resilience4j (Rate limiting & Retry)
- GitHub API

## API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/documentator` | POST | Generate README.md for a repository |
| `/` | POST | Generate architecture diagrams |
| `/actuator/health` | GET | Health check endpoint |

### Request Format

All endpoints accept JSON with a `url` field:

```json
{
  "url": "https://github.com/username/repository.git"
}
```

## Contributing

Contributions are welcome! Please follow these guidelines:

1. **Fork the repository** and create your feature branch (`git checkout -b feature/amazing-feature`)
2. **Make your changes** with clear, descriptive commit messages
3. **Add tests** for any new functionality
4. **Ensure all tests pass** by running `./mvnw test`
5. **Submit a Pull Request** with a detailed description of your changes

### Development Setup

```bash
# Run tests
./mvnw test

# Run with devtools for hot reload
./mvnw spring-boot:run
```

### Code Style

- Follow standard Java conventions
- Use meaningful variable and method names
- Add JavaDoc comments for public APIs
- Keep methods focused and concise

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

Copyright (c) 2026 juangc-code

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
