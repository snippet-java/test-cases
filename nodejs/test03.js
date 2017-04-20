var parameters = {
	request : {
		intent : {
			name : "RawText",
			slots : { Text : { value : "Echo!" } }
		}
	}
}

function main(params) {
	var replyText = "Please say the phrase that you want me to repeat.";
	if (params.request.type != "LaunchRequest") {
		const intent = params.request.intent;
		
		if (intent.name == "RawText")
			replyText = intent.slots.Text.value.toLowerCase().replace(/\W\s/g, '');
	}
	
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

if (require.main === module) console.log(JSON.stringify(main(parameters),null,2));

exports.main = main;
