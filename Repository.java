
import java.util.*;
import java.text.SimpleDateFormat;

// This class is a respository. It permits adding new commits,retrieving repository
// information, checking for the existence of specific commits, and synchronizing
// with another repository by merging commit histories.
public class Repository {
    private String name;
    private Commit head;
    private int size;


    // Behavior: 
    //   - This method creates a repository.
    // Parameters:
    //   - name: the name of the repository
    // Returns:
    //   - N/A
    // Exceptions:
    //   - If the given name is empty or null, an IllegalArgumentException is thrown.
    public Repository(String name){
        if (name == null || name == ""){
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.head = null;
        this.size = 0;
    }

    // Behavior: 
    //   - This method retrieves the first repository version.
    // Parameters:
    //   - N/A
    // Returns:
    //   - String: the id of the first repository version.
    // Exceptions:
    //   - N/A
    public String getRepoHead(){
        if (this.head == null) {
            return null;
        }
        return this.head.id;
    }

    // Behavior: 
    //   - This method retrieves the size of the repository.
    // Parameters:
    //   - N/A
    // Returns:
    //   -int: the size of the repository.
    // Exceptions:
    //   - N/A
    public int getRepoSize(){
        return this.size;
    }


    // Behavior: 
    //   - This method represents the respository and the commits, if any, made in text.
    // Parameters:
    //   - N/A
    // Returns:
    //   -String: the name of the repository, if the version is null, no commits.
    // otherwise the name of the repositsory and its current version are returned to text.
    // Exceptions:
    //   - N/A
    public String toString(){
        if (this.head == null){
            return this.name + " - No commits";
        }
        return this.name + " - Current head: " +  this.head.toString();
    }

    // Behavior: 
    //   - This method checks if a commit exists in the repository.
    // Parameters:
    //   - N/A
    // Returns:
    //   - boolean: true if the id of the commit already exists, otherwise false.
    // Exceptions:
    //   - if the targetId is null, an IllegalArgumentException is thrown.
    public boolean contains(String targetId){
        if (targetId == null){
            throw new IllegalArgumentException();
        }
        Commit current = head;
        while (current != null){
            if(current.id.equals(targetId)){
                return true;
            }
            current = current.past; 
        }
        return false;
    }
        
    

    // Behavior: 
    //   - This method retreieves the history of commits made in this respository
    //  up to the specified number of commits that exist within the size of this respository.
    // Parameters:
    //   - n: number of the most recent commits
    // Returns:
    //   - String: the history.
    // Exceptions:
    //   - if the number of the most recent commits is negative or zero, 
    // an IllegalArgumentException is thrown.
    public String getHistory(int n){
        if (n <= 0){
            throw new IllegalArgumentException();
        }
        String history = "";
        Commit current = this.head;
        while(n > 0 && current != null){
            history += current.toString() + "\n";
            current = current.past;
            n--;
        }
        return history;
    }

    // Behavior: 
    //   - This method adds a commit to the respository.
    // Parameters:
    //   - message: the description of changes made in the commit
    // Returns:
    //   - String: the description of changes made in the commit.
    // Exceptions:
    //   - if the description of changes made in the commit is null, 
    // an IllegalArgumentException is thrown.
    public String commit(String message){
        if(message == null){
            throw new IllegalArgumentException();
        }
        this.size++;
        Commit commitMessage = new Commit(message, this.head);
        this.head = commitMessage;
        return commitMessage.id;
        
    }

    // Behavior: 
    //   - This method removes a commit from the respository.
    // Parameters:
    //   - targetId: the Id of the commit made.
    // Returns:
    //   - boolean: true if the commit that is going to be removed exists, otherwise false.
    // Exceptions:
    //   - if the targetId is null, an IllegalArgumentException is thrown.
    public boolean drop(String targetId){
        if (targetId == null){
            throw new IllegalArgumentException();
        }
        if (head != null && head.id.equals(targetId)) {
            head = head.past;
            this.size--; 
            return true;
        }
        Commit current = head;
        while (current != null && current.past != null) {
            if (current.past.id.equals(targetId)) {
                current.past = current.past.past;
                this.size--;
                return true;
            }
            current = current.past;
        }
        return false;
    }

    // Behavior: 
    //   - This method merges commits from two different respositories
    //  in order by most recent commit to oldest commit into one.
    // Parameters:
    //   - other: the other repository.
    // Returns:
    //   - N/A
    // Exceptions:
    //   - if the other repository is null, an IllegalArgumentException is thrown.
    public void synchronize(Repository other){
        if (other == null){
            throw new IllegalArgumentException();
        }
        this.size += other.size;
        other.size = 0;

        if (this.head == null){
            this.head = other.head;
            other.head = null;
        } 
        else if (other.head != null) {
            Commit temp;
            if(this.head.timeStamp < other.head.timeStamp){
                temp = other.head.past;
                other.head.past = this.head;
                this.head = other.head;
                other.head = temp;
            }

            Commit curr = this.head;
            while(curr.past != null && other.head != null){
                if(curr.past.timeStamp < other.head.timeStamp){
                    temp = other.head.past;
                    other.head.past = curr.past;
                    curr.past = other.head;
                    other.head = temp;
                
                }
                curr = curr.past;
            }

            if (curr.past == null) {
                curr.past = other.head;
                other.head = null;
            }
        }
    }

    /**
     * DO NOT MODIFY
     * A class that represents a single commit in the repository.
     * Commits are characterized by an identifier, a commit message,
     * and the time that the commit was made. A commit also stores
     * a reference to the immediately previous commit if it exists.
     *
     * Staff Note: You may notice that the comments in this 
     * class openly mention the fields of the class. This is fine 
     * because the fields of the Commit class are public. In general, 
     * be careful about revealing implementation details!
     */
    public static class Commit {

        private static int currentCommitID;

        /**
         * The time, in milliseconds, at which this commit was created.
         */
        public final long timeStamp;

        /**
         * A unique identifier for this commit.
         */
        public final String id;

        /**
         * A message describing the changes made in this commit.
         */
        public final String message;

        /**
         * A reference to the previous commit, if it exists. Otherwise, null.
         */
        public Commit past;

        /**
         * Constructs a commit object. The unique identifier and timestamp
         * are automatically generated.
         * @param message A message describing the changes made in this commit. Should be non-null.
         * @param past A reference to the commit made immediately before this
         *             commit.
         */
        public Commit(String message, Commit past) {
            this.id = "" + currentCommitID++;
            this.message = message;
            this.timeStamp = System.currentTimeMillis();
            this.past = past;
        }

        /**
         * Constructs a commit object with no previous commit. The unique
         * identifier and timestamp are automatically generated.
         * @param message A message describing the changes made in this commit. Should be non-null.
         */
        public Commit(String message) {
            this(message, null);
        }

        /**
         * Returns a string representation of this commit. The string
         * representation consists of this commit's unique identifier,
         * timestamp, and message, in the following form:
         *      "[identifier] at [timestamp]: [message]"
         * @return The string representation of this collection.
         */
        @Override
        public String toString() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(timeStamp);

            return id + " at " + formatter.format(date) + ": " + message;
        }

        /**
        * Resets the IDs of the commit nodes such that they reset to 0.
        * Primarily for testing purposes.
        */
        public static void resetIds() {
            Commit.currentCommitID = 0;
        }
    }
}