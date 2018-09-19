const origin = window.location.origin;
// audio recognizer related
const msKey = '4df708c0fee649599bc5c9cbe24a763a';
const recognitionMode = 0;
const language = 'zh-CN';
const format = 0;
const chatPane = $('#conversation');
var recognizerConfig = new SDK.RecognizerConfig(
    new SDK.SpeechConfig(
        new SDK.Context(
            new SDK.OS(navigator.userAgent, "Browser", null),
            new SDK.Device("SpeechSample", "SpeechSample", "1.0.00000"))),
    recognitionMode,
    language, // Supported languages are specific to each recognition mode. Refer to docs.
    format);

var authentication = new SDK.CognitiveSubscriptionKeyAuthentication(msKey);
var recognizer;
var recognizerStarting = false;

function RecognizerStop(SDK, recognizer) {
    // recognizer.AudioSource.Detach(audioNodeId) can be also used here. (audioNodeId is part of ListeningStartedEvent)
    recognizerStarting = false;
    recognizer.AudioSource.TurnOff();
}

function RecognizerStart(){
    console.log('start to recognize')
    recognizer = SDK.CreateRecognizer(recognizerConfig, authentication);
    recognizerStarting = true;
    recognize();
}

function recognize(){
    if(!recognizerStarting){
        console.log('recognizer is not starting');
        return;
    }
	recognizer.Recognize((event) => {
        /*
         Alternative syntax for typescript devs.
         if (event instanceof SDK.RecognitionTriggeredEvent)
        */
        switch (event.Name) {
            case "RecognitionTriggeredEvent" :
                UpdateStatus("Initializing");
                break;
            case "ListeningStartedEvent" :
                UpdateStatus("Listening");
                break;
            case "RecognitionStartedEvent" :
                UpdateStatus("Listening_Recognizing");
                break;
            case "SpeechStartDetectedEvent" :
                UpdateStatus("Listening_DetectedSpeech_Recognizing");
                console.log(JSON.stringify(event.Result)); // check console for other information in result
                break;
            case "SpeechHypothesisEvent" :
                UpdateRecognizedHypothesis(event.Result.Text, false);
                console.log(JSON.stringify(event.Result)); // check console for other information in result
                break;
            case "SpeechFragmentEvent" :
                UpdateRecognizedHypothesis(event.Result.Text, true);
                console.log(JSON.stringify(event.Result)); // check console for other information in result
                break;
            case "SpeechEndDetectedEvent" :
            	console.log("SpeechEndDetectedEvent");
                OnSpeechEndDetected();
                UpdateStatus("Processing_Adding_Final_Touches");
                console.log(JSON.stringify(event.Result)); // check console for other information in result
                break;
            case "SpeechSimplePhraseEvent" :
            	console.log("SpeechSimplePhraseEvent");
//                UpdateRecognizedPhrase(JSON.stringify(event.Result, null, 3));
            	UpdateRecognizedPhrase(event);
                break;
            case "SpeechDetailedPhraseEvent" :
            	console.log("SpeechDetailedPhraseEvent");
//            	UpdateRecognizedPhrase(JSON.stringify(event.Result, null, 3));
            	UpdateRecognizedPhrase(event);
                break;
            case "RecognitionEndedEvent" :
            	console.log("RecognitionEndedEvent");
                // OnComplete();
                UpdateStatus("Idle");
                recognize();
                console.log(JSON.stringify(event)); // Debug information
                break;
            default:
                console.log(JSON.stringify(event)); // Debug information
        }
    })
    .On(() => {
        // The request succeeded. Nothing to do here.
    },
    (error) => {
        console.error(error);
    });
}

function UpdateStatus(s){
	console.log(s)
}

function UpdateRecognizedHypothesis(s){
	console.log('UpdateRecognizedHypothesis: ' + s)
}

function OnSpeechEndDetected(){
	console.log('OnSpeechEndDetected')
}

function UpdateRecognizedPhrase(event){
	if(event.Result && event.Result.RecognitionStatus === 'Success'){
        let txt = event.Result.DisplayText;
        sendMessage({
            id: 'speak',
            content: txt
        })
    }else{
        // console.log()
	}
}

function sendMessage(data){
    if(window.parent === window){
        console.warn('not in a iframe, abort sending message');
        return;
    }
    window.parent.postMessage(JSON.stringify(data), origin);
}

const HANDLERS = {
    'startTranslate': RecognizerStart,
    'stopTranslate': function(){
        RecognizerStop(SDK, recognizer);
    }
};
const NO_SUCH_HANDLER = function(d){
    console.warn('no handler for this message: ' + JSON.stringify(d));
}
window.addEventListener('message', event => {
    if(event.origin !== origin){
        console.error('invalid origin');
        return;
    }
    let data = JSON.parse(event.data);
    (HANDLERS[data.id] || NO_SUCH_HANDLER)(data);
}, false)