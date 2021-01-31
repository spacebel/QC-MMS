/**
 * ESE Project
 * Utility JS Class for Inter Portlet Communication.
 * @author Christophe Noel
 * @date 10 02 2014 
 */

// Solve issue "Object doesn't support this action" of line 57 "new Event" with IE
(function () {

  if ( typeof window.CustomEvent === "function" ) return false;

  function CustomEvent ( event, params ) {
    params = params || { bubbles: false, cancelable: false, detail: null };
    var evt = document.createEvent( 'CustomEvent' );
    evt.initCustomEvent( event, params.bubbles, params.cancelable, params.detail );
    return evt;
   }

  window.CustomEvent = CustomEvent;
})();

function CommunicationUtil() {
}
;

/**
 * Check if the given instanceId is one of the target of the given message
 * @param {String} instanceid
 * @param {Object} message
 */
CommunicationUtil.isTarget = function (instanceId, message) {
    if (message.targets === 'All' || message.targets === 'all') {
        return true;
    }
    if (message.targets === instanceId) {
        return true;
    }
    return false;
};

/**
 * Send a topic message to a specified target
 * 
 * @param {String}
 *            topic
 * @param {String}
 *            instanceid
 * @param {String}
 *            target
 * @param {Object}
 *            messageData
 */
CommunicationUtil.sendMessageToTarget = function (topic, instanceId, target,
        messageData) {
    //console.log("CommunicationUtil.sendMessageToTarget:" + "topic " + topic + " instanceid:" + instanceId + " target: " + target + " messageData:" + JSON.stringify(messageData));        
    if (typeof(Event) === 'function') {
        response = new Event(topic);
    } else {
        response = document.createEvent('Event');
        response.initEvent(topic, true, true);
    }
    
    response.sender = instanceId;
    response.targets = target;
    response.data = messageData;
    //alert("Dispatching Event "+topic)
    document.dispatchEvent(response);

};

/**
 * Send a topic message to all components (via gluelogic if active)
 * 
 * @param {String}
 *            topic
 * @param {String}
 *            instanceid
 * @param {Object}
 *            messageData
 */
CommunicationUtil.sendMessageToAll = function (topic, instanceId, messageData) {
    var target = CommunicationUtil.getTarget();
    CommunicationUtil.sendMessageToTarget(topic, instanceId, target,
            messageData);
};

/**
 * Return 'gluelogic' when the Gluelogic is active, 'all' if not
 */
CommunicationUtil.getTarget = function () {
    if (typeof Gluelogic != "undefined") {
        if (Gluelogic.active) {
            return 'gluelogic';
        }
    }
    return 'all';
};

/**
 * sendDiscoveryResponse
 * 
 * @instanceId : the id of the component
 * @param {Array.
 *            <String>} publishedTopic list of published topics
 * @param {Array.
 *            <String>} subscribedTopic list of subscribed topics
 */
CommunicationUtil.sendDiscoveryResponse = function (instanceId, publishedTopics,
        subscribedTopics) {
    var target = 'gluelogic';
    var data = new Object();
    data.publishedTopics = publishedTopics;
    data.subscribedTopics = subscribedTopics;
    // Send the discovery Response
    CommunicationUtil.sendMessageToTarget("dashboard.discovery.response",
            instanceId, target, data);

};

/**
 * sendStateResponse
 * 
 * @param {Object}
 *            stateId : the stateId contained in request message
 * @param {String}
 *            instanceId id of the component
 * @param {Object}
 *            stateData state Object to store at Dashboard manager side
 */
CommunicationUtil.sendStateResponse = function (stateId, instanceId, stateData) {
    var target = 'gluelogic';
    var data = new Object();
    data.stateId = stateId;
    data.stateData = stateData;
    CommunicationUtil.sendMessageToTarget("dashboard.state.response",
            instanceId, target, data);

};

/**
 * @param {} instanceId : instance id of the component
 * @param {} topic : the topic requested (may be null)
 * @param {} mode : "last" to get the last message, "all" to choose between messages
 */
CommunicationUtil.sendRecordedMessageRequest = function (instanceId, topic, mode) {
    var target = "gluelogic";
    var data = new Object();
    data.topic = topic;
    data.mode = mode;
    CommunicationUtil.sendMessageToTarget("dashboard.stack.message.request",
            instanceId, target, data);
};

