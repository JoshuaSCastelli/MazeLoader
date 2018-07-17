// File             : MazeLoader.java
// Author           : David W. Collins Jr.
// Date Created     : 03/01/2016
// Last Modified    : 03/08/2016
// Description      : This is the MazeLoader file for Math 271 where students
//                    will implement the recursive routine to "solve" the maze.
package mazeloader;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;


/** This is the main class that defines the window to load the maze
 * 
 * @author collindw
 */
public class window {
    
    private JFrame window;
    private Scanner fileToRead;
    private JPanel[][] grid;
    private static final int SMALL_CELL_SIZE = 40;
    private static final int LARGE_CELL_SIZE = 20;
    private static final Color START_COLOR = Color.BLACK;
    private static final Color WALL_COLOR = Color.BLUE.darker();
    private static final Color PATH_COLOR = Color.GREEN.brighter();
    private static final Color OPEN_COLOR = Color.WHITE;
    private static final Color BAD_PATH_COLOR  = Color.RED;
    private static int ROW;
    private static int COL;
    private String data;
    private Point start;
    private boolean allowMazeUpdate;
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem[] loadMaze;
    private Timer timer;
    boolean truePath=false;
    
    /** Default constructor - initializes all private values
     * 
     */
    public window() {
        // Intialize other "stuff"
        start = new Point();
        allowMazeUpdate = true;
        timer = new Timer(100, new TimerListener());
        
        // Create the maze window
        window = new JFrame("Maze Program");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Need to define the layout - as a grid depending on the number
        // of grid squares to use. Open the file and read in the size.
        try {
            fileToRead = new Scanner(new File("maze.txt"));
            ROW = fileToRead.nextInt();
            COL = fileToRead.nextInt();
            
            //add file chooser here.
        }
        catch(FileNotFoundException e) {
            JOptionPane.showMessageDialog(window,"Cannot find maze to solve. " +
                    "\nMake sure file is called maze.txt", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        // Now establish the Layout - appropriate to the grid size
        if(ROW < 20 || COL < 20)
            window.setSize(ROW*SMALL_CELL_SIZE/2, COL*SMALL_CELL_SIZE/2);
        else
            window.setSize(ROW*LARGE_CELL_SIZE, COL*LARGE_CELL_SIZE/2);
        
        window.setLayout(new GridLayout(ROW, COL));
        window.setLocationRelativeTo(null);
        grid= new JPanel[ROW][COL];
        data = fileToRead.nextLine();
        for(int i=0; i<ROW; i++) {
            data = fileToRead.nextLine();
            for(int j=0; j<COL; j++) {
                grid[i][j] = new JPanel();
                grid[i][j].setName("" + i + ":" + j);
                grid[i][j].addMouseListener(new MazeListener());
                if(data.charAt(j) == '*')
                    grid[i][j].setBackground(WALL_COLOR);
                else
                    grid[i][j].setBackground(OPEN_COLOR);
                window.add(grid[i][j]);
            }
        }
        fileToRead.close();

        // Add the menu to the window
        menuBar = new JMenuBar();
        menu = new JMenu("Load Maze...");
        loadMaze = new JMenuItem[2];
        loadMaze[0] = new JMenuItem("Load New Maze from another file...");
        loadMaze[0].addActionListener(new LoadMazeFromFile());
        loadMaze[1] = new JMenuItem("Load New Maze from current maze...");
        loadMaze[1].addActionListener(new ReloadCurrentMaze());
        menu.add(loadMaze[0]);
        menu.add(loadMaze[1]);
        menuBar.add(menu);
        window.setJMenuBar(menuBar);
        
        // Finally, show the maze
        window.setResizable(false);
        window.setVisible(true);
    }
    
    /** MazeListener class reacts to mouse presses - only when the current
     *  block that is clicked is a valid starting point within the maze.
     */
    private class MazeListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        /** mousePressed method defines the (x,y) coordinate of the starting
         *  square within the maze. Note: the start Point object does NOT
         *  reference the pixel location, rather the matrix location.
         * @param e - the MouseEvent created upon mouse click.
         */
        @Override
        public void mousePressed(MouseEvent e) {
            if(((JPanel)e.getSource()).getBackground().equals(OPEN_COLOR) &&
                    !timer.isRunning()) {
                data = ((JPanel)e.getSource()).getName();
                start.x = Integer.parseInt(data.substring(0,data.indexOf(":")));
                start.y = Integer.parseInt(data.substring(data.indexOf(":")+1));
                System.out.println("Starting at grid: " + start);
                grid[start.x][start.y].setBackground(START_COLOR);
                timer.start();
                // Find the maze solution
                if(!findPath(start))
                    JOptionPane.showMessageDialog(window,"Cannot exit maze.");
                else
                    JOptionPane.showMessageDialog(window, "Maze Exited!");
                timer.stop();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
        
    }
    
    /** findPath is the recursive routine to find the solution through the maze
     * 
     * @param p - the current Point in the maze
     * @return whether or not a solution has been found.
     */
    public boolean findPath(Point p)  {
        //check east, north, west, south.
        
        grid[p.x][p.y].setBackground(PATH_COLOR);
        window.repaint();
        if(p.y==COL-1||p.y==0||p.x==ROW-1||p.x==0){
            return true;
        }
        
        if(grid[p.x][p.y+1].getBackground()==OPEN_COLOR){//check east
            //grid[p.x][p.y+1].setBackground(PATH_COLOR);
            //p.y++;
            //window.repaint();
            truePath=findPath(new Point(p.x, p.y+1));
            if(truePath){
                return true;
            }
        }
        if(grid[p.x-1][p.y].getBackground()==OPEN_COLOR){//check north
            //grid[p.x-1][p.y].setBackground(PATH_COLOR);
            //p.x--;
            //window.repaint();
            //findPath(new Point(p.x-1,p.y));
            truePath=findPath(new Point(p.x-1, p.y));
            if(truePath){
                return true;
            }
        }
        if(grid[p.x][p.y-1].getBackground()==OPEN_COLOR){//check west
            //grid[p.x][p.y-1].setBackground(PATH_COLOR);
            //p.y--;
            //window.repaint();
            //findPath(new Point(p.x,p.y-1));
            truePath=findPath(new Point(p.x, p.y-1));
            if(truePath){
                return true;
            }
        }
        if(grid[p.x+1][p.y].getBackground()==OPEN_COLOR){//check south
            //grid[p.x+1][p.y].setBackground(PATH_COLOR);
            //p.x++;
            //window.repaint();
            //findPath(new Point(p.x+1,p.y));
            truePath=findPath(new Point(p.x+1, p.y));
            if(truePath){
                return true;
            }
        }
        else{
            grid[p.x][p.y].setBackground(BAD_PATH_COLOR);
            window.repaint();
            /*if(grid[p.x][p.y-1].getBackground()==PATH_COLOR||//west
                    grid[p.x][p.y-1].getBackground()==START_COLOR){
                p.y--;
                window.repaint();
                findPath(p);
            }
            if(grid[p.x-1][p.y].getBackground()==PATH_COLOR||//north
                    grid[p.x-1][p.y].getBackground()==START_COLOR){
                p.x--;
                window.repaint();
                findPath(p);
            }
            if(grid[p.x][p.y+1].getBackground()==PATH_COLOR||//east
                    grid[p.x][p.y+1].getBackground()==START_COLOR){
                p.y++;
                window.repaint();
                findPath(p);
            }
            if(grid[p.x+1][p.y].getBackground()==PATH_COLOR||//south
                    grid[p.x+1][p.y].getBackground()==START_COLOR){
                p.x++;
                window.repaint();
                findPath(p);
            }  
            */
        }
            return false;
    }
    
    /** ReloadCurrentMaze class listens to menu clicks - simply
     *  wipes the current state of the maze, and resets ability to start.
     */
    private class ReloadCurrentMaze implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            for(int i=0; i<ROW; i++)
                for(int j=0; j<COL; j++)
                    if(grid[i][j].getBackground().equals(PATH_COLOR) ||
                       grid[i][j].getBackground().equals(BAD_PATH_COLOR)||
                            grid[i][j].getBackground().equals(START_COLOR))
                         grid[i][j].setBackground(OPEN_COLOR);
        }
    }
    
    /** LoadMazeFromFile class listens to menu clicks - if the student
     *  wishes to earn extra credit, implement this method by utilizing a
     *  FileChooser to allow the user to choose the maze file, rather than
     *  have it hard-coded in the program as "maze.txt"
     * 
     *   Extra credit -- use JFileChooser class
     */
    private class LoadMazeFromFile implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
           JOptionPane.showMessageDialog(window, "Not implemented yet.", 
                   "Extra Credit", JOptionPane.INFORMATION_MESSAGE);
        }
        
    }
    
    /** TimerListener class - currently being investigated to slow down
     *  the maze solution path.
     */
    private class TimerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            allowMazeUpdate = true;
        }
    }
}
