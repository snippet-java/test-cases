var params = {
	request : {
		type : "RawText",
		intent : {
			name : "RawText",
			slots : { Text : { value : "Echo!" } }
		}
	}
}

function main(params) {
	var replyText = "Please say the phrase that you want me to repeat.";
	if (params.request.type != "LaunchRequest") {
		var intent = params.request.intent;
		var sessionAttributes = params.session.attributes;
		
		if (intent.name == "RawText")
			replyText = intent.slots.Text.value.toLowerCase().replace(/\W\s/g, '');
	}
	
	console.log("replyText : " + replyText);

	var outputSpeech = {
		"type": "SSML",
		"ssml": "<speak>" + replyText + "</speak>"
	};

	return {
		"version": "1.0",
		"response": {
			outputSpeech,
			"reprompt": { outputSpeech },
			"shouldEndSession": false
		}
	}
}

if (require.main === module) main(params);

exports.main = main;
