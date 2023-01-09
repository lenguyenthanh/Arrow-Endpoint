rootProject.name = "arrow-endpoint"
include("core", "schema-reflect")

// clients
include("clients", ":clients:ktor-client")

// servers
include("servers", ":servers:spring-web-server")

// docs
include("docs", ":docs:openapi-docs")
