package clueGame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

public class ComputerPlayer extends Player {
	private char lastRoom = ' ';
	private Solution unprovenSolution = null;

	public ComputerPlayer(String playerName, int row, int column, Color color) {
		super(playerName, row, column, color);
	}
	
	public BoardCell pickLocation(Set<BoardCell> set) {
		List<BoardCell> visitedRooms = new ArrayList<BoardCell>(), all = new ArrayList<BoardCell>();
		for (BoardCell bc : set) {
			if (bc.isRoom() && bc.getInitial() != lastRoom)
				visitedRooms.add(bc);
			all.add(bc);
		}
		
		if (visitedRooms.size() > 0) {
			BoardCell choice = visitedRooms.get((int)(Math.random() * visitedRooms.size()));
			lastRoom = choice.getInitial();
			row = choice.getRow();
			column = choice.getColumn();
			
			return choice;
		} else {
			return all.get((int)(Math.random() * all.size()));
		}
	}
	
	public Solution makeAccusation() {
		// TODO
		return null;
	}
	
	public Solution createSuggestion() {
		// Initializing
		Solution newSolution = new Solution();
		
		String[] allWeapons = Board.getInstance().getAllWeapons();
		Player[] allPlayers = Board.getInstance().getAllPlayers();
		
		List<String> notSeenWeapons = new ArrayList<String>();
		List<Player> notSeenPersons = new ArrayList<Player>();
		
		// Finding not seen stuff
		for (int i = 0; i < allWeapons.length; i++) {
			boolean notSeen = true;	
			for (Card c : seenWeapons) {
				if (c.getCardName().equals(allWeapons[i])) {
					notSeen = false;
				}
			}
			if (notSeen) {
				notSeenWeapons.add(allWeapons[i]);
			}
		}
		for (int i = 0; i < allPlayers.length; i++) {
			boolean notSeen = true;	
			for (Card c : seenPersons) {
				if (c.getCardName().equals(allPlayers[i].getName())) {
					notSeen = false;
				}
			}
			if (notSeen){
				notSeenPersons.add(allPlayers[i]);
			}
		}
		
		newSolution.person = notSeenPersons.get((int)(Math.random() * notSeenPersons.size())).getName();
		newSolution.weapon = notSeenWeapons.get((int)(Math.random() * notSeenWeapons.size()));
		newSolution.room = Board.getInstance().getRoomName(lastRoom);
				
		
		return newSolution;
	}
	
	@Override
	public boolean makeMove(Set<BoardCell> targets) {
		// Make an accusation, if possible
		if (unprovenSolution != null) {
			boolean win = Board.getInstance().checkAccusation(unprovenSolution);
			
			if (win) {JOptionPane.showMessageDialog(Board.getInstance(), getName() + " Wins! The correct solution was: " + unprovenSolution); System.exit(0);}
  			else {JOptionPane.showMessageDialog(Board.getInstance(), getName() + " made an incorrect accusation: " + unprovenSolution);}
		}
		
		// Move ComputerPlayer to new cell
		BoardCell newCell = pickLocation(targets);
		row = newCell.getRow();
		column = newCell.getColumn();
		
		// Make a suggestion if moving into a room
		if (newCell.isRoom()) {
			// Generate a suggestion
			Solution solution = createSuggestion();
			Card card = Board.getInstance().handleSuggestion(solution);
			
			// Move the player of interest
			Player[] player = Board.getInstance().getAllPlayers();
			for (Player p : player) {
				if (p.getName().equals(solution.person)) {
					p.column = column;
					p.row = row;
				}
			}
			
			if (card == null && !getCards().contains((Object) Board.getInstance().getRoomName(newCell.getInitial()))) unprovenSolution = solution;
			else unprovenSolution = null;
		}
		
		return false;
	}
	
	/* For Testing Purposes */
	public void setLastRoomLocation(Character room) {this.lastRoom = room; }
	public void setSeenWeapons(List<Card> weapons) {Player.seenWeapons = weapons; }
	public void setSeenPersons(List<Card> players) {Player.seenPersons = players; }
}
