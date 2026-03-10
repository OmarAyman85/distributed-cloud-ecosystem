# ==============================================================================
# Platform Infrastructure: Enterprise Onboarding
# ==============================================================================
# Purpose: Professional setup for the distributed cloud ecosystem.
# ==============================================================================

# Colors for professional output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BLUE}==================================================================${NC}"
echo -e "${BLUE}Starting Enterprise Platform Onboarding...${NC}"
echo -e "${BLUE}==================================================================${NC}"

# 1. Dependency Check
echo -e "\n${YELLOW}Checking Technical Requirements...${NC}"
r
check_dep() {
    if command -v $1 >/dev/null 2>&1; then
        echo -e "  [OK] $1: $( $1 $2 | head -n 1 )"
    else
        echo -e "  [FAIL] $1 is NOT installed. ${RED}Aborting.${NC}"
        exit 1
    fi
}

check_dep "docker" "--version"
check_dep "java" "-version"
check_dep "mvn" "-version"
check_dep "node" "--version"
check_dep "ng" "version"

# 2. Network Orchestration
echo -e "\n${YELLOW}Orchestrating Docker Infrastructure...${NC}"
if docker network ls | grep -q "platform-network"; then
    echo -e "  [OK] platform-network already exists."
else
    docker network create platform-network
    echo -e "  [OK] platform-network created successfully."
fi

# 3. Local Environment Configuration
echo -e "\n${YELLOW}Initializing Environment Templates...${NC}"
find . -name "*.example" | while read -r template; do
    target="${template%.example}"
    if [ ! -f "$target" ]; then
        cp "$template" "$target"
        echo -e "  [FILE] Created: ${target}"
    else
        echo -e "  [SKIP] Skipped: ${target} (already exists)"
    fi
done

echo -e "\n${GREEN}==================================================================${NC}"
echo -e "${GREEN}Onboarding Complete! Your local environment is verified.${NC}"
echo -e "Next Step: Run './devops-utilities/scripts/ecosystem_doctor.sh' to check status."
echo -e "${GREEN}==================================================================${NC}"
