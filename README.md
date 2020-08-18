# WasherDryerBackend
post: washer/addMachine: Input: {"item_id" : "1"; "type" : "washer"; "address" : "1"; "item_condition" : "available"; "model" : "a"; "brand" : "a" } Output： { "result": "sucess" }

post: washer/register Input： {"user_id" : "11"; "phone_number" : "11"; "password" : "111" } Output： { "status": "User Already Exists" }

get: washer/logout Output： { "status": "logout successfully" }

post: washer/login Input： {"user_id" : "11"; "password" : "111" } Output: { "user_id": "11", "name": "11", "status": "OK" }

get： washer/login(check session valid) Output： { "status": "Invalid Session" }

get: washer/getAllMachines [ { "condition": "available", "address": "1", "item_id": "1", "model": "a", "type": "washer", "brand": "a" } ]
