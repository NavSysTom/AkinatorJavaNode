import java.io.*;
import java.util.Scanner;

class GuessingGame {

    BinaryTree<String> root = new BinaryTree<String>();


    /*
    * Constructor which creates the initial tree that we will use when there is no save file and when we start off this project
    * Once the tree is created, we run the game*/
    public GuessingGame() {

        initialTree();
        playGame();

    }

    /*
    * This creates a small sample of trees and nodes and a simple yes and no question to create the initial tree which the user will guess from
    * */
    private void initialTree() {
        BinaryTree<String> petDog = new BinaryTree<>("Dog");
        BinaryTree<String> petCat = new BinaryTree<>("Cat");

        BinaryTree<String> dogQuestion = new BinaryTree<>("Does it bark? " ,petDog,petCat);

        BinaryTree<String> wildAnimalLizard = new BinaryTree<>("Lizard");
        BinaryTree<String> wildAnimalEagle = new BinaryTree<>("Eagle");

        BinaryTree<String> scaleQuestion = new BinaryTree<>("Does it have scales? ", wildAnimalLizard, wildAnimalEagle);

        BinaryTree<String> manMadeOil = new BinaryTree<>("Oil");
        BinaryTree<String> manMadeComputer= new BinaryTree<>("Computer");

        BinaryTree<String> plasticQuestion = new BinaryTree<>("Can you make plastic from it ?", manMadeOil, manMadeComputer);

        BinaryTree<String> beachSand = new BinaryTree<>("Sand");
        BinaryTree<String> tree = new BinaryTree<>("Tree");

        BinaryTree<String> beachQuestion = new BinaryTree<>("Can you find it on the beach? ", beachSand, tree);


        BinaryTree<String> aliveQuestion = new BinaryTree<>("Can you keep it as a pet? ", dogQuestion, scaleQuestion);
        BinaryTree<String> notAliveQuestion = new BinaryTree<>("Is this man-made ?", plasticQuestion, beachQuestion);

        root.setTree("Is the item you are thinking of alive? ", aliveQuestion, notAliveQuestion);

    }



    private void playGame() {
        /*
        * We check if the file exists and if it does, we ask the user if they want to use it for their next run of the game so they
        * can keep guessing and keep adding onto more and more nodes and trees into this yes or no game
        * If we do not want to use the loaded version, it will just run the initial tree that we implemented above
        * If they say yes to the load saved game, it will give all the previous iterations that have been asked for a better guessing game*/

        File treeFile = new File("binaryTree.txt");
        if (treeFile.exists()) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Would you like to load the saved game? (Yes or No): ");
            String userInput = scanner.nextLine().toLowerCase();
            if (userInput.toLowerCase().equals("yes")) {
                try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("binaryTree.txt"))) {
                    root.setRootNode((BinaryNodeInterface<String>) inputStream.readObject());
                    System.out.println("Game loaded.");
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Error loading game.");
                }
            }
        }



        Scanner scanner = new Scanner(System.in);
        String userInput = ""; //this is what we store the users input into

        BinaryNodeInterface<String> nodeIteration = root.getRootNode();
        while (true) {
            nodeIteration = root.getRootNode(); // Reset nodeIteration to the root node
            while (!nodeIteration.isLeaf()) { // navigate the binary tree by iterating through non-leaf nodes based on user input
                if (nodeIteration.getLeftChild().getData().equals("NULL") && nodeIteration.getRightChild().getData().equals("NULL")) {
                    break;
                }

                System.out.print(nodeIteration.getData() + " (Yes or No): ");// prompt user with question
                userInput = scanner.nextLine();// take user input


                // navigate the binary tree based on user input
                switch (userInput.toLowerCase()) {
                    case "yes":
                        nodeIteration = nodeIteration.getLeftChild(); //if its a yes, we go to the left of the tree
                        break;
                    case "no":
                        nodeIteration = nodeIteration.getRightChild(); //if its a right, we go to the left of the tree
                        break;
                    default:
                        System.out.println("Please Type Correct Input [Only Correct Inputs Are: Yes or No ]"); //if they say something that isn't yes or no
                        continue;
                }
            }

            /*make final guess and give user options for next things to do if the final guess was correct, like save this tree for later use
            * or to load it or to quit the program entirely*/

            System.out.print("My final guess is " + nodeIteration.getData() + " (Yes or No): ");
            userInput = scanner.nextLine();

            if (userInput.toLowerCase().equals("yes")) {

                System.out.println("\nGood Job, We guessed correctly! ");
                System.out.println("Would you like to do one of the following: ");
                System.out.println("Play Again?    [Type Again for another game] ");
                System.out.println("Save the tree? [Type Save for save tree] ");
                System.out.println("Load the tree? [Type Load for load tree] ");
                System.out.println("Quit?          [Type Quit to exit] "); //print to the user what options they have after they guessed correctly
                userInput = scanner.hasNextLine() ? scanner.nextLine().toLowerCase() : "quit";

                if (userInput.equals("again")) {
                    System.out.println();
                    playGame();
                } else if (userInput.equals("save")) {
                    saveGame();
                } else if (userInput.equals("load")) {
                    loadGame();
                } else {
                    System.exit(0);
                }
            } else { /*
            if it doesn't get the answer correctly it will ask the user for the correct answer
            and a question that should along the new answer that they created that would make it accurate to that object
            it will then update the whole binary tree with that new node and question allowing for a bigger and bigger node constantly*/
                System.out.println("I give up. What is it?");
                String correctAnswer = scanner.nextLine();
                System.out.println("Please provide a question that would distinguish a " + nodeIteration.getData() + " from a " + correctAnswer); //this will ask you for a question that will distinguish the new object you wanted and the one it was guessing
                String newQuestion = scanner.nextLine();

                BinaryNode<String> oldNode = (BinaryNode<String>) nodeIteration;
                BinaryNode<String> newAnswer = new BinaryNode<>(correctAnswer);
                BinaryNode<String> oldAnswer = new BinaryNode<>(oldNode.getData());

                oldNode.setData(newQuestion);
                oldNode.setLeftChild(newAnswer);
                oldNode.setRightChild(oldAnswer);

                saveGame(); // save the changes to the tree
                System.out.println("Thank you! Let's play again.");
                playGame();
            }
        }
    }

    /*
    Saves current state of the binary tree by writing the root node of the tree into a file called binaryTree.txt using ObjectOutputStream
    Catches exceptions if there is an error saving it
    * */
        private void saveGame() {
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("binaryTree.txt"))) {
                outputStream.writeObject(root.getRootNode());
            } catch (IOException e) {
                System.out.println("Error saving game.");
            }
        }


    /*
    * Loads the state of the binaryTree.txt file using ObjectInputStream so that we can keep building on the binaryTree making it bigger and bigger
    * each time we save and load it back
    * also catches exception for when the loading of the file didn't work*/
    private void loadGame() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("binaryTree.txt"))) {
            root.setRootNode((BinaryNodeInterface<String>) inputStream.readObject());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading game.");
        }
    }


    public static void main(String[] args) {
        new GuessingGame(); // runs constructor
    }

}