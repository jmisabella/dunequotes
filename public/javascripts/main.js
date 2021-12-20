    var webSocket;
    var messageInput;

    function init() {
      var host = location.origin.replace(/^https/, 'wss').replace(/^http/, 'ws');
      webSocket = new WebSocket(`${host}/ws`);
      webSocket.onopen = onOpen;
      webSocket.onclose = onClose;
      webSocket.onmessage = onMessage;
      webSocket.onerror = onError;
      // $("#quote").focus();
      // $("#message-input").focus();
    }

    function onOpen(event) {
      consoleLog("CONNECTED to server");
      sendInitToServer();
      quote();
    }

    function onClose(event) {
      consoleLog("DISCONNECTED from server");
      consoleLog("Re-initializing a new fresh connection so server will be available for next action");
      init();
    }

    function onError(event) {
      consoleLog("ERROR: " + event.data);
      consoleLog("ERROR: " + JSON.stringify(event));
    }

    function onMessage(event) {
      console.log(event.data);
      let receivedData = JSON.parse(event.data);
      console.log("New Data: ", receivedData);
      $("#quote").html(receivedData.body.quote);
      $("#source").html(receivedData.body.source);
    }

    function consoleLog(message) {
      console.log("New message: ", message);
    }

    window.addEventListener("load", init, false);

    $("#toggle-random").click(function (e) {
      quote();
    });
    
    function quote() {
      var currentFunction = $("#toggle-random").html();
      if (currentFunction == null || currentFunction == "") {
        currentFunction = "Featured";
      }
      var nextFunction = "";
      if (currentFunction == "Featured") {
        nextFunction = "Random";
      } else {
        nextFunction = "Featured";
      }
      $("#toggle-random").html(nextFunction);

      messageInput = currentFunction.toLowerCase();

      // if the trimmed message was blank, return now
      if ($.trim(messageInput) == "") {
        return false;
      }

      // create the message as json
      let jsonMessage = {
          message: messageInput
      };

      // send our json message to the server
      sendToServer(jsonMessage);
    } 

    // send the message when the user presses the <enter> key while in the textarea
    $(window).on("keydown", function (e) {
      if (e.which == 13) {
        quote();
        return false;
      }
    });

    function sendInitToServer() {
      messageInput = "featured";
      if ($.trim(messageInput) == "") {
        return false;
      }
      let jsonMessage = {
        message: messageInput
      };
      sendToServer(jsonMessage);
    }

    // send the data to the server using the WebSocket
    function sendToServer(jsonMessage) {
      if(webSocket.readyState == WebSocket.OPEN) {
        consoleLog("SENT: " + jsonMessage.message);
        webSocket.send(JSON.stringify(jsonMessage));
      } else {
        consoleLog("Could not send data. Websocket is not open.");
      }
    }
