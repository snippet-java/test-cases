var parameters = {
    "text": "hello my friend",
    "fromLanguage": "en",
    "toLanguage": "es",
    "username": "",
    "password": "",
    "url": 'https://gateway.watsonplatform.net/language-translator/api'
}
const request = require("request");
const templateUrl = "https://gist.githubusercontent.com/snippet-java/fd3aa1c2ab893bf8e1bcd90073ceab99/raw";

function main(params) {
    if (params.__ow_method == "get") {
        var inputs = "";
        for (i in parameters) {
            inputs += "<input name='" + i + "' placeholder='" + i + "' value='" + parameters[i] + "' style='width:100%; padding:10px'><br/>"
        }
        
        return new Promise(function (resolve, reject) {
            request(templateUrl, function(err, res, body) {
                return resolve({html:body.replace("{{inputs}}", inputs)});
            })
        });
    }

    return new Promise(function (resolve, reject) {
        var newParams = {};
        for (var i in parameters) {
            newParams[i] = params[i]
        }
        translate(newParams, function (result) {
            return resolve(result);
        });
    });
}

function translate(params, cb) {
    const watson = require('watson-developer-cloud');

    var language_translator;

    if (params.username != null && params.username != "" && params.password != null && params.password != "" && params.url != null && params.url != "") {
        var credentials = {
            username: params.username,
            password: params.password,
            url: params.url,
            version: 'v2'
        }
        language_translator = watson.language_translator(credentials);
    } else if (process.env.services) {
        var services = JSON.parse(process.env.services);
        if (services.language_translator && services.language_translator[0] && services.language_translator[0].credentials) {
            var credentials = services.language_translator[0].credentials;
            language_translator = watson.language_translator(credentials);
        } else {
            return cb({ err: "missing credentials" });
        }
    } else {
        return cb({ err: "missing credentials" });
    }

    var options = {
        text: params.text,
        source: params.fromLanguage,
        target: params.toLanguage
    };
    language_translator.translate(options, function (err, translation) {
        if (err)
            return cb({ err });

        return cb(translation);
    });
}

if (require.main === module) {
    translate(parameters, function (result) {
        console.log(JSON.stringify(result, null, 2));
    });
}

exports.main = main;
