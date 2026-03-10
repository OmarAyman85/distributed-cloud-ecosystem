# ==============================================================================
# Platform Infrastructure: Ecosystem Doctor
# ==============================================================================
# Purpose: Diagnostic tool to verify the status of all microservices & infra.
# ==============================================================================

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${YELLOW}Running Ecosystem Diagnostics...${NC}\n"

# 1. Infrastructure Check
echo -e "${YELLOW}Checking Core Infrastructure (Docker):${NC}"
containers=("postgres" "mysql" "zipkin" "prometheus")

for service in "${containers[@]}"; do
    if docker ps | grep -q "$service"; then
        echo -e "  [OK] $service: UP"
    else
        echo -e "  [FAIL] $service: DOWN"
    fi
done

# 2. Service Discovery Check (Eureka)
echo -e "\n${YELLOW}Checking Service Registry (Eureka):${NC}"
if curl -s -o /dev/null -w "%{http_code}" http://localhost:8761 > /dev/null; then
    echo -e "  [OK] Eureka Server (8761): REACHABLE"
else
    echo -e "  [FAIL] Eureka Server (8761): UNREACHABLE"
fi

# 3. Routing Layer Check (Gateway)
echo -e "\n${YELLOW}Checking API Gateway:${NC}"
if curl -s -o /dev/null -w "%{http_code}" http://localhost:8080 > /dev/null; then
    echo -e "  [OK] Gateway (8080): REACHABLE"
else
    echo -e "  [FAIL] Gateway (8080): UNREACHABLE"
fi

echo -e "\n${GREEN}Diagnostic Complete.${NC}"
