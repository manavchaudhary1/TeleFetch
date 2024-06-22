<h1 align="center">TeleFetch</h1> 

## Overview
An Application for downloading documents from [Telegram](https://telegram.org) Private Channels that you are part of from telegram.
Build upon [SpringBoot](https://spring.io/projects/spring-boot) with help of [Spring Boot Starter for Telegram](https://github.com/p-vorobyev/spring-boot-starter-telegram) which is based on [TDLib](https://github.com/tdlib/td).

## Content

- [Requirements](#requirements)
- [Installation](#installation)
- [Configuration properties](#configuration)
- [Running Api calls](#api)

<a name="requirements"></a>
###  Requirements
| Technology                   | Version                                                                               |
|------------------------------|---------------------------------------------------------------------------------------|
| JRE                          | `Java 21 ` and above                                                                  |
| TDLib                        | [1.8.29](https://github.com/p-vorobyev/spring-boot-starter-telegram/blob/master/libs) |
| Spring Boot                  | 3.3.0                                                                                 |
| spring boot starter telegram | [1.11.0](https://github.com/p-vorobyev/spring-boot-starter-telegram/releases/tag/1.11.0)|

TDLib [depends](https://github.com/tdlib/td#dependencies) on:

- C++14 compatible compiler (Clang 3.4+, GCC 4.9+, MSVC 19.0+ (Visual Studio 2015+), Intel C++ Compiler 17+)
- OpenSSL
- zlib

**Additional Requirement**
- curl
- Maven
- jq
- grep (optional)

<a name="installation"></a>
### Installation

1) Downloading Dependencies for TDLib for Linux 
```
sudo apt-get install zlib1g-dev libssl-dev
```
2) Clone source code:
```shell
git clone https://github.com/manavchaudhary1/telefetch.git
cd TeleFetch
```


4) Install mvn repo
```bash
./mvnw clean install
```

> pom is configured for linux_x64 OS.

You can find compiled libraries for several platforms in [libs](https://github.com/manavchaudhary1/TeleFetch/tree/master/libs) directory of the source code from the latest release.
If you haven't found a library for your OS and architecture, you can build it yourself following this [instructions](https://github.com/p-vorobyev/spring-boot-starter-telegram/blob/master/libs/build/readme.md).


<a name="configuration"></a>
## Configuration properties


**Getting your API Keys:**
The very first step requires you to obtain a valid Telegram API key (API id/hash pair):
1.  Visit  [https://my.telegram.org/apps](https://my.telegram.org/apps)  and log in with your Telegram Account.
2.  Fill out the form to register a new Telegram application.
3.  Done! The API key consists of two parts:  **api_id**  and  **api_hash**.


Mandatory properties for autoconfiguration:

> Edit in src/main/resources/application.properties

| property                                          | type   | description                                                                                                      |
|---------------------------------------------------|--------|------------------------------------------------------------------------------------------------------------------|
| `spring.telegram.client.api-id`                   | int    | Your Api Id                                                                                                      |
| `spring.telegram.client.api-hash`                 | String | Your Api Hash                                                                                                    |
| `spring.telegram.client.phone`                    | String | The phone number of the user, in international format.                                                           |
| `spring.telegram.client.database-encryption-key`  | String | Encryption key for the database. If the encryption key is invalid, then an error with code 401 will be returned. |

Run TeleFetch :
```
mvn spring-boot:run 
```
- application log

```text
INFO 10647 --- [   TDLib thread] .s.t.c.u.UpdateAuthorizationNotification : Please enter authentication code
```
When You See This Log in Terminal Run with authentication code.
```
curl -X POST http://localhost:8080/api/authorization/code \
    -H "Content-Type: application/json" \
    -d '{"value": "your_authentication_code"}'
```

If You have enabled 2FA , Then You have to send Password.
```text
INFO 10647 --- [   TDLib thread] .s.t.c.u.UpdateAuthorizationNotification : Please enter password
```
```
curl -X POST http://localhost:8080/api/authorization/password \
    -H "Content-Type: application/json" \
    -d '{"value": "your_password"}'
```
After completing authentication you can check weather you were authorized or not using :
```
curl -X GET http://localhost:8080/api/authorization/status
```

**Getting chat id:**

**1. Using web telegram:**
1. Open https://web.telegram.org/?legacy=1#/im
2. Now go to the chat/channel and you will see the URL as something like
    - `https://web.telegram.org/?legacy=1#/im?p=u853521067_2449618633394` here `853521067` is the chat id.
    - `https://web.telegram.org/?legacy=1#/im?p=@somename` here `somename` is the chat id.
    - `https://web.telegram.org/?legacy=1#/im?p=s1301254321_6925449697188775560` here take `1301254321` and add `-100` to the start of the id => `-1001301254321`.
    - `https://web.telegram.org/?legacy=1#/im?p=c1301254321_6925449697188775560` here take `1301254321` and add `-100` to the start of the id => `-1001301254321`.

**2. Using Telegram Desktop Client**
1. Open Any Telegram CLient ; i personally recommend [64Gram](https://flathub.org/apps/io.github.tdesktop_x64.TDesktop).
2. Open Channel you want to download documents from

   ![](https://github.com/manavchaudhary1/TeleFetch/blob/master/img/example.png)

<a name="api"></a>
## Running Api calls

1) Fetching Chat History
```bash
curl -X POST http://localhost:8080/api/chat \
     -H "Content-Type: application/json" \
     -d '{"chatId": <your_chatId>, "limit": <how_many_last_message_history_you_want>}' | jq
```
The limit is placed by TDLib i had no control over it and if only one message is fetched then try again restrictions placed by TDLib 😐.

> You can also place 'cmd > messages.json' for downloading fetched data in JSON File for easy readability.

2) Downloading Messages

Make sure that both api calls are in same instance of Telegram Client cause file.id is randomly assigned and changes every instance.
```bash
curl -X GET http://localhost:8080/api/download/{fileId}
```

- If you want to save multiple files , use below script:
```
#!/bin/bash

# Function to send a GET request and print the response
send_request() {
    local fileId=$1
    response=$(curl -s -X GET "http://localhost:8080/api/download/${fileId}")
    echo "Response for fileId ${fileId}: ${response}"
}

# Prompt the user to input fileIds
echo "Enter fileIds separated by space:"
read -a fileIds

# Loop through each fileId and send the request
for fileId in "${fileIds[@]}"; do
    send_request $fileId
done
```
- Give Execute Permission for script
```
chmod +x {script_name}.sh
```
- Run the Script:
```bash
bash {script_name}.sh
```
- Input the fileIds when prompted. For example:
```
Enter fileIds separated by space:
12345 67890 11223
```
