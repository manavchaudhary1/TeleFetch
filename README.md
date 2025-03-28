<h1 align="center">TeleFetch</h1> 

## Overview
An Application for downloading documents from [Telegram](https://telegram.org) Private Channels that you are part of from telegram.
Build upon [SpringBoot](https://spring.io/projects/spring-boot) with help of [Spring Boot Starter for Telegram](https://github.com/p-vorobyev/spring-boot-starter-telegram) which is based on [TDLib](https://github.com/tdlib/td).

## Content

- [Requirements](#requirements)
- [Installation](#installation)
- [Configuration properties](#configuration-properties)
- [Login and Authorization](#login-and-authorization)
- [Running Api calls](#running-api-calls)

<a name="requirements"></a>
###  Requirements
| Technology                   | Version                                                                                  |
|------------------------------|------------------------------------------------------------------------------------------|
| JRE                          | `Java 21 ` and above                                                                     |
| TDLib                        | [1.8.29](https://github.com/p-vorobyev/spring-boot-starter-telegram/blob/master/libs)    |
| Spring Boot                  | 3.3.0                                                                                    |
| spring boot starter telegram | [1.11.0](https://github.com/p-vorobyev/spring-boot-starter-telegram/releases/tag/1.11.0) |

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

1) Downloading Dependencies for TDLib for

   - Debian based distro
   ```
   sudo apt-get install zlib1g-dev libssl-dev curl grep jq
   ```
   - RedHat based distro
   ```
   sudo dnf install zlib-devel openssl-devel curl grep jq
   ```
2) Specify `github` server with your credentials in `settings.xml` for Apache Maven. See GitHub [docs](https://docs.github.com/en/enterprise-cloud@latest/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens#about-personal-access-tokens) how to generate personal token.

   - settings.xml are located in `~/.m2` directory.
      ```xml
      <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                        http://maven.apache.org/xsd/settings-1.0.0.xsd">
   
      <servers>
          <server>
              <id>github</id>
              <username>GITHUB_LOGIN_USERNAME</username>
              <password>GITHUB_TOKEN</password>
          </server>
      </servers>
   
      </settings>
      ```

   Token  should have `read:packages` scope.

3) Clone source code:
   ```shell
   git clone https://github.com/manavchaudhary1/TeleFetch.git
   cd TeleFetch
   ```

4) Install mvn repo
   ```bash
   mvn clean install
   ```

You can find scripts to build libraries for several platforms in [libs](https://github.com/manavchaudhary1/TeleFetch/tree/master/libs/build) directory of the source code from the latest release.
If you haven't found a library for your OS and architecture, you can build it yourself following this [instructions](https://github.com/manavchaudhary1/TeleFetch/tree/master/libs/build/readme.md).


<a name="configuration-properties"></a>
## Configuration properties


**Getting your API Keys:**
The very first step requires you to obtain a valid Telegram API key (API id/hash pair):
1.  Visit  [https://my.telegram.org/apps](https://my.telegram.org/apps)  and log in with your Telegram Account.
2.  Fill out the form to register a new Telegram application.
3.  Done! The API key consists of two parts:  **api_id**  and  **api_hash**.


Mandatory properties for autoconfiguration:

```bash
chmod +x edit_entries.sh
bash edit_entries.sh
```

| property                     | type   | description                                                                                                      |
|------------------------------|--------|------------------------------------------------------------------------------------------------------------------|
| `${TELEGRAM_API_ID}`         | int    | Your Api Id                                                                                                      |
| `${TELEGRAM_API_HASH}`       | String | Your Api Hash                                                                                                    |
| `${TELEGRAM_PHONE}`          | String | The phone number of the user, in international format.                                                           |
| `${TELEGRAM_ENCRYPTION_KEY}` | String | Encryption key for the database. If the encryption key is invalid, then an error with code 401 will be returned. |


<a name="login-and-authorization"></a>
## Login and Authorization

Run TeleFetch :
```
mvn spring-boot:run  -P <OS-Arch-declared-in-pom.xml>
```

| OS-Arch        | Declared in pom.xml |
|----------------|---------------------|
| Linux-arm      | linux-arm64         |
| Linux-x86_64   | linux-x64           |
| MAC os aarch   | macos-silicon       |
| MAC os x86_64  | macos-x64           |   
| Windows-x86_64 | windows-x64         |

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

Now we are ready to fetch chat history and download files from telegram.
We can either use api calls or use web interface to download files.
Head over to [http://localhost:8080](http://localhost:8080) to use web interface and for api calls refer [Api calls](#running-api-calls).

**Getting chat id:**

**1. Using web telegram:**
1. Open https://web.telegram.org/?legacy=1#/im
2. Now go to the chat/channel and you will see the URL as something like
    - `https://web.telegram.org/?legacy=1#/im?p=u853521067_2449618633394` here `853521067` is the chat id.
    - `https://web.telegram.org/?legacy=1#/im?p=@somename` here `somename` is the chat id.
    - `https://web.telegram.org/?legacy=1#/im?p=s1301254321_6925449697188775560` here take `1301254321` and add `-100` to the start of the id => `-1001301254321`.
    - `https://web.telegram.org/?legacy=1#/im?p=c1301254321_6925449697188775560` here take `1301254321` and add `-100` to the start of the id => `-1001301254321`.

**2. Using Telegram Desktop Client**
1. Open Any Telegram Client ; i personally recommend [64Gram](https://github.com/TDesktop-x64/tdesktop).
2. Open Channel you want to download documents from

   ![](https://github.com/manavchaudhary1/TeleFetch/blob/master/img/example.png)

**3. Getting Chat ID from Telefetch Web Interface**
1. Open [localhost:8080/groupid](http://localhost:8080/groupid)
   ![](https://github.com/manavchaudhary1/TeleFetch/blob/master/img/groupid.png)
2. TeleFetch will show you recent 1000 chats you have been part of.
3. We can search and copy ChatId from here and head to [localhost:8080/download](https://localhost:8008/download) and start downloading chats.


<a name="running-api-calls"></a>
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

3) Checking Download Progress

After initiating downloading file , we can check download progress , using:
```
curl -X GET http://localhost:8080/api/download/progress | jq
```

Response will we like this
```text
[
    {
        "progress":90,
        "expectedSize":137193473,
        "downloadedSize":123863040,
        "fileId":3606,
        "status":"In Progress"
    }.
    {
        "progress":100,
        "expectedSize":137193473,
        "downloadedSize":137193473,
        "fileId":3606,
        "status":"Completed"
    }
]
```
