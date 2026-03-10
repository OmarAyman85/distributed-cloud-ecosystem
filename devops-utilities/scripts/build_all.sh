#!/bin/bash

# ==============================================================================
# Platform Infrastructure: Master Build Orchestrator
# ==============================================================================
# Purpose: Compiles and builds the entire ecosystem in the correct order.
# ==============================================================================

YELLOW='\033[1;33m'
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${YELLOW}Starting Universal Ecosystem Build...${NC}"

build_module() {
    echo -e "\nBuilding: $1..."
    mvn clean install -DskipTests -f "$1/pom.xml"
    if [ $? -eq 0 ]; then
        echo -e "[OK] $1 Build Successful."
    else
        echo -e "[FAIL] $1 Build Failed."
        exit 1
    fi
}

# 1. Platform Infrastructure (Must be first)
build_module "core-infrastructure/discovery-server"
build_module "core-infrastructure/config-server"
build_module "core-infrastructure/api-gateway"
build_module "core-infrastructure/authy-service"

# 2. Shared Services
build_module "shared-services/delivery-service"
build_module "shared-services/media-service"
build_module "shared-services/notification-service"
build_module "shared-services/payment-service"

# 3. Domain Services
build_module "domain-services/finance/savvy-service"
build_module "domain-services/fashion/simuclothing-service"
build_module "domain-services/automotive/carloger-service"

echo -e "\n${GREEN}Build operations completed successfully!${NC}"
