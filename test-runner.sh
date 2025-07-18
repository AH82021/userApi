#!/bin/bash

# Spring Boot Testing Script
# This script helps run different types of tests in your Spring Boot application

echo "Spring Boot Testing Helper"
echo "=========================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

function run_unit_tests() {
    echo -e "${YELLOW}Running Unit Tests...${NC}"
    ./mvnw test -Dtest="**/*Test" -DfailIfNoTests=false
}

function run_integration_tests() {
    echo -e "${YELLOW}Running Integration Tests...${NC}"
    ./mvnw test -Dtest="**/*IntegrationTest" -DfailIfNoTests=false
}

function run_all_tests() {
    echo -e "${YELLOW}Running All Tests...${NC}"
    ./mvnw test
}

function run_with_coverage() {
    echo -e "${YELLOW}Running Tests with Coverage...${NC}"
    ./mvnw test jacoco:report
}

function run_specific_test() {
    echo -e "${YELLOW}Enter the test class name (e.g., UserServiceTest):${NC}"
    read test_class
    ./mvnw test -Dtest="$test_class"
}

function clean_and_test() {
    echo -e "${YELLOW}Cleaning and Running Tests...${NC}"
    ./mvnw clean test
}

function show_test_report() {
    echo -e "${YELLOW}Opening Test Report...${NC}"
    if [[ "$OSTYPE" == "darwin"* ]]; then
        open target/site/jacoco/index.html
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        xdg-open target/site/jacoco/index.html
    else
        echo "Test report available at: target/site/jacoco/index.html"
    fi
}

# Main menu
while true; do
    echo ""
    echo "Select an option:"
    echo "1. Run Unit Tests"
    echo "2. Run Integration Tests"
    echo "3. Run All Tests"
    echo "4. Run Tests with Coverage"
    echo "5. Run Specific Test"
    echo "6. Clean and Test"
    echo "7. Show Test Report"
    echo "8. Exit"
    echo ""
    read -p "Enter your choice (1-8): " choice

    case $choice in
        1) run_unit_tests ;;
        2) run_integration_tests ;;
        3) run_all_tests ;;
        4) run_with_coverage ;;
        5) run_specific_test ;;
        6) clean_and_test ;;
        7) show_test_report ;;
        8) echo -e "${GREEN}Goodbye!${NC}"; exit 0 ;;
        *) echo -e "${RED}Invalid option. Please try again.${NC}" ;;
    esac
done
