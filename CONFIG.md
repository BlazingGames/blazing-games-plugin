# jda

## enabled

boolean, true if you want discord chat sync to be enabled. all values are ignored if this is false

## token

string, your discord bot token

## link-channel

int, id of the channel to send event notifications to and to listen for chat messages

## console-channel

int, id of the channel to send console logs to and listen for messages with console commands

## webhook

string, url of the discord webhook to send chat messages to. should be in the same channel as link-channel



# logging

## log-error

boolean, true if you want to log errors

## log-info

boolean, true if you want to log regular information to the console

## log-debug

boolean, true if you want to log debug info, useful if you're developing the plugin

## notify-ops-on-error

boolean, true if you want to send a message to online operators when an error occurs (requires log-error to be true)



# computing

## local

### disable-computers

boolean, true if you want to disable computer management. all other computing settings are ignored if this is true

### privileges

#### chunkloading

boolean, true if you want to allow chunkloading via an upgrade

#### net

boolean, true if you want to allow real internet access

## microsoft

### spoof-ms-server

boolean, true if you want to allow anyone to login as any username/uuid without logging in. useful for debugging.

### client-id

string, your microsoft app's client id. ignored if spoof-ms-server is true

### client-secret

string, your microsoft app'sclient secret. ignored if spoof-ms-server is true

## jwt

### secret-key

string, your jwt secret key. set to randomize-on-server-start if you want to let the server generate one

### secret-key-is-password

boolean, true if you want to use the secret key as a password, instead of base64 decoding it

## services

### blazing-api and blazing-wss

#### enabled

boolean, true if you want to allow this web server to be reached on the internet. all other service-spesific values are ignored if this is false

#### find-at

url, where the service can be located on the internet

#### bind

##### port

int, port to bind the service to

##### https

###### enabled

boolean, if the service should use an https server instead of an http one. all other https values are ignored if this is false

###### password

string, password for the p12 file

###### file

string, path to the p12 cert file relative to the `/plugins/blazinggames` folder

#### proxy

##### in-use

boolean, true if the service is behind a reverse proxy. allows for getting ip addresses from headers and limiting connecting ips. all other proxy values are ignored if this is false

##### ip-address-header

boolean, header to get ip addresses from

##### allow-all

boolean, true if you want to allow all connecting ips

##### allowed-ipv4 and allowed-ipv6

string[], ip addresses that are allowed to connect to the service. ignored if allow-all is true



# docs

information to show under the docs at the root of the computing-api.

all values are ignored if computing.services.blazing-api.enabled is false

## official-instance

name, url: information of the official link to the website to manage computers at

## user-links and developer-links

links to show on the homepage.

array of object: name, url

## notice

information to show under the notice bar at the bottom of the homepage.

show: if the noticebar should be shown

title, description: text to show

button-title, button-url: action button
