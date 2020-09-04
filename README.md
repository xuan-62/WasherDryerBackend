# WasherDryerBackend
post: washer/addMachine: Input: {"item_id" : "1"; "type" : "washer"; "address" : "1"; "item_condition" : "available"; "model" : "a"; "brand" : "a" } Output： { "result": "sucess" }

post: washer/register Input： {"user_id" : "11"; "phone_number" : "11"; "password" : "111" } Output： { "status": "User Already Exists" }

get: washer/logout Output： { "status": "logout successfully" }

post: washer/login Input： {"user_id" : "11"; "password" : "111" } Output: { "user_id": "11", "name": "11", "status": "OK" }

get： washer/login(check session valid) Output： { "status": "Invalid Session" }

get: washer/getAllMachines [ { "condition": "available", "address": "1", "item_id": "1", "model": "a", "type": "washer", "brand": "a" } ]

post: washer/report Input: {"user_id" : "11"; "item_id" : "1"; "issueType" : "a"; "issue": "abc"}

post: washer/changeMachineStatus Input: {"user_id" : "11"; "status" : "reserve/start/available"; "item_id" : "1"} Output: {"status":"OK"}

post: washer/remindUser: {"user_id" : "11"; "item_id" : "1"; "to_user_id" : "1111(the user using this machine)"};
