package com.yourname.villageraimod.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MessageGenerator {
    private static final Random RANDOM = new Random();
    
    public static String generateMessage(VillagerEmotion emotion, float relationship, int conversationDepth, VillagerRelationship.MessageType messageType) {
        List<String> possibleMessages = new ArrayList<>();
        
        switch (messageType) {
            case GREETING:
                possibleMessages = generateGreetings(emotion, relationship, conversationDepth);
                break;
            case FAREWELL:
                possibleMessages = generateFarewells(emotion, relationship);
                break;
            case QUESTION:
                possibleMessages = generateQuestions(emotion, relationship);
                break;
            case COMMAND_RESPONSE:
                possibleMessages = generateCommandResponses(emotion, relationship);
                break;
            case SMALL_TALK:
                possibleMessages = generateSmallTalk(emotion, relationship, conversationDepth);
                break;
            case TRADE_OFFER:
                possibleMessages = generateTradeOffers(emotion, relationship);
                break;
            case COMPLAINT:
                possibleMessages = generateComplaints(emotion, relationship);
                break;
            case COMPLIMENT:
                possibleMessages = generateCompliments(emotion, relationship);
                break;
        }
        
        if (possibleMessages.isEmpty()) {
            return emotion.getDefaultMessage();
        }
        
        return possibleMessages.get(RANDOM.nextInt(possibleMessages.size()));
    }
    
    private static List<String> generateGreetings(VillagerEmotion emotion, float relationship, int depth) {
        List<String> greetings = new ArrayList<>();
        
        if (relationship > 0.8f) {
            greetings.add("My dear friend! How wonderful to see you!");
            greetings.add("Welcome back, my trusted companion!");
            greetings.add("Oh, it's you! You always brighten my day!");
            if (depth > 5) {
                greetings.add("Hello again! I was just thinking about our last conversation.");
                greetings.add("You're here! I have so much to tell you since we last spoke.");
            }
        } else if (relationship > 0.6f) {
            greetings.add("Hello there, good friend!");
            greetings.add("Nice to see you again!");
            greetings.add("Greetings! How are you doing today?");
        } else if (relationship > 0.4f) {
            greetings.add("Oh, hello there.");
            greetings.add("Good day to you.");
            greetings.add("Greetings, traveler.");
        } else if (relationship > 0.2f) {
            greetings.add("What do you want?");
            greetings.add("Oh... it's you again.");
            greetings.add("I suppose you need something.");
        } else {
            greetings.add("Stay away from me!");
            greetings.add("I don't want to talk to you!");
            greetings.add("Leave me alone!");
        }
        
        return greetings;
    }
    
    private static List<String> generateFarewells(VillagerEmotion emotion, float relationship) {
        List<String> farewells = new ArrayList<>();
        
        if (relationship > 0.8f) {
            farewells.add("Take care, my dear friend! Come back soon!");
            farewells.add("Until we meet again! Stay safe out there!");
            farewells.add("Farewell for now! I'll miss your company!");
        } else if (relationship > 0.6f) {
            farewells.add("Goodbye! Safe travels!");
            farewells.add("See you later!");
            farewells.add("Take care out there!");
        } else if (relationship > 0.4f) {
            farewells.add("Goodbye.");
            farewells.add("Until next time.");
            farewells.add("Farewell.");
        } else if (relationship > 0.2f) {
            farewells.add("Finally, some peace.");
            farewells.add("About time you left.");
            farewells.add("Don't let the door hit you on the way out.");
        } else {
            farewells.add("Good riddance!");
            farewells.add("Stay away!");
            farewells.add("And don't come back!");
        }
        
        return farewells;
    }
    
    private static List<String> generateQuestions(VillagerEmotion emotion, float relationship) {
        List<String> questions = new ArrayList<>();
        
        if (relationship > 0.7f) {
            questions.add("How has your adventure been treating you?");
            questions.add("Tell me, what brings you joy these days?");
            questions.add("Have you discovered anything interesting lately?");
            questions.add("What's the most exciting thing that's happened to you recently?");
        } else if (relationship > 0.5f) {
            questions.add("How are you doing today?");
            questions.add("What brings you to our village?");
            questions.add("Is there anything I can help you with?");
        } else if (relationship > 0.3f) {
            questions.add("What do you need?");
            questions.add("Why are you here?");
            questions.add("Is this important?");
        } else {
            questions.add("What do you want now?");
            questions.add("Why won't you leave me alone?");
            questions.add("What's your problem?");
        }
        
        return questions;
    }
    
    private static List<String> generateCommandResponses(VillagerEmotion emotion, float relationship) {
        List<String> responses = new ArrayList<>();
        
        if (relationship > 0.75f) {
            responses.add("Of course! I'd be happy to help you!");
            responses.add("Anything for you, my friend!");
            responses.add("Consider it done!");
            responses.add("I trust your judgment completely.");
        } else if (relationship > 0.6f) {
            responses.add("Sure, I can do that.");
            responses.add("Alright, I'll help you out.");
            responses.add("I suppose I can assist you.");
        } else if (relationship > 0.4f) {
            responses.add("I'm not sure about this...");
            responses.add("Do I really have to?");
            responses.add("Fine, but I'm not happy about it.");
        } else if (relationship > 0.2f) {
            responses.add("I don't want to do that.");
            responses.add("Why should I listen to you?");
            responses.add("Find someone else to bother.");
        } else {
            responses.add("Absolutely not!");
            responses.add("I refuse to help you!");
            responses.add("You can't make me do anything!");
        }
        
        return responses;
    }
    
    private static List<String> generateSmallTalk(VillagerEmotion emotion, float relationship, int depth) {
        List<String> smallTalk = new ArrayList<>();
        
        if (relationship > 0.7f && depth > 3) {
            smallTalk.add("You know, I've been thinking about what you said last time...");
            smallTalk.add("Did you know that I used to be quite the adventurer myself?");
            smallTalk.add("I remember when this village was much smaller.");
            smallTalk.add("Sometimes I wonder what lies beyond those distant mountains.");
            smallTalk.add("The seasons here remind me of my childhood.");
        } else if (relationship > 0.5f) {
            smallTalk.add("The weather's been quite nice lately, don't you think?");
            smallTalk.add("Have you seen how the crops are growing this season?");
            smallTalk.add("This village has been my home for many years.");
            smallTalk.add("I enjoy the peaceful life here.");
        } else if (relationship > 0.3f) {
            smallTalk.add("The days are all the same here.");
            smallTalk.add("Not much happens in this village.");
            smallTalk.add("I keep to myself mostly.");
        } else {
            smallTalk.add("I don't want to chat.");
            smallTalk.add("I have nothing to say to you.");
            smallTalk.add("Why are you trying to talk to me?");
        }
        
        return smallTalk;
    }
    
    private static List<String> generateTradeOffers(VillagerEmotion emotion, float relationship) {
        List<String> trades = new ArrayList<>();
        
        if (relationship > 0.8f) {
            trades.add("For you, my friend, I have special prices!");
            trades.add("I saved my best items just for you!");
            trades.add("Let me give you a discount on that!");
        } else if (relationship > 0.6f) {
            trades.add("I have some good deals today!");
            trades.add("Would you like to see what I'm selling?");
            trades.add("I think you'll find my prices fair.");
        } else if (relationship > 0.4f) {
            trades.add("I suppose I could sell you something.");
            trades.add("Here's what I have available.");
            trades.add("Standard prices apply.");
        } else if (relationship > 0.2f) {
            trades.add("I'm not sure I want to trade with you.");
            trades.add("My prices are higher for people like you.");
            trades.add("I might have something, but it'll cost extra.");
        } else {
            trades.add("I won't trade with you!");
            trades.add("Take your business elsewhere!");
            trades.add("I don't want your money!");
        }
        
        return trades;
    }
    
    private static List<String> generateComplaints(VillagerEmotion emotion, float relationship) {
        List<String> complaints = new ArrayList<>();
        
        if (relationship < 0.3f) {
            complaints.add("You've been nothing but trouble!");
            complaints.add("I'm tired of your behavior!");
            complaints.add("Why can't you just leave us in peace?");
            complaints.add("You're making life difficult for everyone!");
        } else if (relationship < 0.5f) {
            complaints.add("I'm not happy with how things have been going.");
            complaints.add("You could treat us better, you know.");
            complaints.add("I wish you would be more considerate.");
        } else {
            complaints.add("I hope you understand my concerns.");
            complaints.add("Perhaps we could handle things differently next time?");
            complaints.add("I just wanted to mention something that's been bothering me.");
        }
        
        return complaints;
    }
    
    private static List<String> generateCompliments(VillagerEmotion emotion, float relationship) {
        List<String> compliments = new ArrayList<>();
        
        if (relationship > 0.8f) {
            compliments.add("You're absolutely wonderful!");
            compliments.add("I admire your courage and kindness!");
            compliments.add("You make this world a better place!");
            compliments.add("I'm so grateful to have you as a friend!");
        } else if (relationship > 0.6f) {
            compliments.add("You're quite skilled, I must say!");
            compliments.add("I appreciate what you do for our village!");
            compliments.add("You seem like a good person.");
        } else if (relationship > 0.4f) {
            compliments.add("You're... not terrible, I suppose.");
            compliments.add("I've seen worse adventurers.");
            compliments.add("You have your moments.");
        } else {
            // No compliments for low relationship
            compliments.add("I have nothing nice to say to you.");
        }
        
        return compliments;
    }
} 