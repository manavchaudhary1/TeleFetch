#!/bin/bash

# Define the file path
file_path="src/main/resources/application.properties"

# Check if the file exists
if [ ! -f "$file_path" ]; then
    echo "File not found: $file_path"
    exit 1
fi

# Prompt the user for input
read -p "Enter your API ID: " api_id
read -p "Enter your API Hash: " api_hash
read -p "Enter your phone number (with country code): " phone_number
read -p "Enter your encryption key: " encryption_key

# Use sed to replace placeholders with actual values
sed -i.bak "s/\${TELEGRAM_API_ID}/$api_id/" $file_path
sed -i.bak "s/\${TELEGRAM_API_HASH}/$api_hash/" $file_path
sed -i.bak "s/\${TELEGRAM_PHONE}/$phone_number/" $file_path
sed -i.bak "s/\${TELEGRAM_ENCRYPTION_KEY}/$encryption_key/" $file_path

# Verify if the replacements were made
if grep -q "$api_id" "$file_path" && grep -q "$api_hash" "$file_path" && grep -q "$phone_number" "$file_path" && grep -q "$encryption_key" "$file_path"; then
    echo "Entries updated successfully."
else
    echo "Failed to update Entries."
fi

# Optionally, remove the backup file created by sed
rm -f ${file_path}.bak
