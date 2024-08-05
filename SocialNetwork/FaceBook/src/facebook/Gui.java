package facebook;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class Gui {
    private User user1;
    private JFrame frame1;
    private int WIDTH = 800;
    private int HEIGHT = 600;
    private String searchText;

    public Gui(User firstUser) {
        this.user1 = firstUser;
        initializeLoginFrame();
    }

    private void initializeLoginFrame() {
        frame1 = new JFrame("Simple Facebook");
        frame1.setSize(WIDTH, HEIGHT);
        frame1.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel idLabel = new JLabel("ID ");
        idLabel.setBounds(300, 250, 60, 20);
        panel.add(idLabel);

        JTextField idText = new JTextField();
        idText.setBounds(360, 250, 120, 20);
        panel.add(idText);

        JLabel pwLabel = new JLabel("Password");
        pwLabel.setBounds(300, 270, 60, 20);
        panel.add(pwLabel);

        JPasswordField pwText = new JPasswordField();
        pwText.setBounds(360, 270, 120, 20);
        panel.add(pwText);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(350, 295, 60, 20);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame1.setVisible(false);
                showHomePage(user1);
            }
        });
        panel.add(loginButton);

        frame1.add(panel);
        frame1.setVisible(true);
    }

    private void showHomePage(User user) {
        JFrame frame = createMainFrame("Facebook Benzeri Arayüz");

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createTopPanel(), BorderLayout.NORTH);
        mainPanel.add(createLeftPanel(user1), BorderLayout.WEST);
        mainPanel.add(createCenterPanel(user), BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JFrame createMainFrame(String title) {
        JFrame frame = new JFrame(title);
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return frame;
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.BLUE);
        topPanel.setPreferredSize(new Dimension(WIDTH, 50));

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Ara");

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchText = searchField.getText();
                searchGui(searchText);
            }
        });

        topPanel.add(searchField);
        topPanel.add(searchButton);
        return topPanel;
    }

    private JPanel createLeftPanel(User user) {
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(200, HEIGHT));
        leftPanel.setBackground(Color.LIGHT_GRAY);
        List<String > groupsName = new ArrayList<>();
        List<Group> groups = new ArrayList<>();
        for(Group g : user.getGroups()){
            groupsName.add(g.getName());
            groups.add(g);
        }
        String[] array = groupsName.toArray(new String[0]);


        JList<String> groupsList = new JList<>(array);
        JScrollPane groupsScrollPane = new JScrollPane(groupsList);
        groupsScrollPane.setPreferredSize(new Dimension(180, HEIGHT - 100));

        groupsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = groupsList.locationToIndex(evt.getPoint());
                    Group groupName = groups.get(index);
                    openGroupPage(groupName,user);
                }
            }
        });

        leftPanel.add(new JLabel("Gruplar"));
        leftPanel.add(groupsScrollPane);
        return leftPanel;
    }

    private JPanel createCenterPanel(User user) {
        JPanel centerPanel = new JPanel(new BorderLayout());

        JPanel wallPanel = new JPanel();
        wallPanel.setLayout(new BoxLayout(wallPanel, BoxLayout.Y_AXIS));

        // Add existing posts
        for (String post : user.getWall().getPosts()) {
            JLabel postLabel = new JLabel(post);
            wallPanel.add(postLabel);
        }

        // Add new post components
        JPanel postPanel = new JPanel();
        postPanel.setLayout(new BoxLayout(postPanel, BoxLayout.X_AXIS));
        JTextField newPostField = new JTextField(20);
        JButton postButton = new JButton("Post");

        postButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newPost = newPostField.getText();
                if (!newPost.isEmpty()) {
                    user.addPost(newPost);
                    // Refresh the center panel to show the new post
                    centerPanel.removeAll();
                    centerPanel.add(createCenterPanel(user), BorderLayout.CENTER);
                    centerPanel.revalidate();
                    centerPanel.repaint();
                }
            }
        });

        postPanel.add(newPostField);
        postPanel.add(postButton);

        // Add post panel to the top of the center panel
        centerPanel.add(postPanel, BorderLayout.NORTH);

        JScrollPane wallScrollPane = new JScrollPane(wallPanel);
        wallScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        centerPanel.add(wallScrollPane, BorderLayout.CENTER);
        return centerPanel;
    }


    private void searchGui(String name) {
        JFrame searchFrame = new JFrame("Kullanıcı Arama Sonuçları");
        searchFrame.setSize(400, 300);
        searchFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));

        List<User> userList = UserManager.getInstance().getUsers();
        boolean userFound = false;

        for (User u : userList) {
            if (u.getName().equalsIgnoreCase(name)) {
                if (u.isSearchVisibility()) {
                    userFound = true;
                    JButton userButton = new JButton(u.getName());
                    userButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if(u.equals(user1)){
                                showHomePage(user1);
                            }
                            else{
                            showUserHomePage(u);}
                        }
                    });
                    searchPanel.add(userButton);
                }
            }
        }

        if (!userFound) {
            JLabel notFoundLabel = new JLabel("Aradığınız kullanıcı bulunamadı.");
            searchPanel.add(notFoundLabel);
        }

        searchFrame.add(searchPanel);
        searchFrame.setVisible(true);
    }



    private void showUserHomePage(User user) {
        JFrame frame = createMainFrame("Facebook Benzeri Arayüz");

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createTopPanel(), BorderLayout.NORTH);
        mainPanel.add(createLeftPanel(user), BorderLayout.WEST);
        mainPanel.add(createUserCenterPanel(user), BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createUserCenterPanel(User user) {
        JPanel centerPanel = new JPanel(new BorderLayout());

        JPanel wallPanel = new JPanel();
        wallPanel.setLayout(new BoxLayout(wallPanel, BoxLayout.Y_AXIS));

        JLabel userNameLabel = new JLabel("Kullanıcı Adı: " + user.getName());
        wallPanel.add(userNameLabel);
        String friendText1 = "Arkadaşlık isteği Gönder";
        String friendText2 = "Arkadaşı çıkar";
        boolean friendship = checkFriendShip(user1,user);
        String buttonMessage1;
        String buttonMessage2 ;
        if (user.isFriendAdding()) {
            if(friendship){
                buttonMessage1 = friendText2;
                buttonMessage2 = friendText1;
            }else {
                buttonMessage1 = friendText1;
                buttonMessage2 = friendText2;
            }
            JButton sendFriendRequestButton = new JButton(buttonMessage1);
            wallPanel.add(sendFriendRequestButton);

            sendFriendRequestButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    sendFriendRequestButton.setText(buttonMessage2);
                    user1.addFriend(user);
                }
            });
        }

        for (String post : user.getWall().getPosts()) {
            JLabel postLabel = new JLabel(post);
            wallPanel.add(postLabel);
        }

        JScrollPane wallScrollPane = new JScrollPane(wallPanel);
        wallScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        centerPanel.add(wallScrollPane, BorderLayout.CENTER);
        return centerPanel;
    }
    public boolean checkFriendShip(User user1,User user2 ){
        return user1.friendCheck(user2);
    }
    public void openGroupPage(Group group, User user) {
        JFrame groupFrame = new JFrame("Group: " + group.getName());
        groupFrame.setSize(600, 400);
        groupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel groupPanel = new JPanel(new BorderLayout());
        JPanel memberPanel = new JPanel();
        memberPanel.setLayout(new BoxLayout(memberPanel, BoxLayout.Y_AXIS));

        for (User member : group.getMembers()) {
            JLabel memberLabel = new JLabel(member.getName());
            memberPanel.add(memberLabel);
        }

        JScrollPane memberScrollPane = new JScrollPane(memberPanel);
        memberScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JButton addButton = new JButton("Add Member");
        JButton removeButton = new JButton("Remove Member");

        JTextField userNameField = new JTextField(20);

        JPanel controlPanel = new JPanel();
        controlPanel.add(userNameField);
        controlPanel.add(addButton);
        controlPanel.add(removeButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userName = userNameField.getText();
                User user = findUserByName(userName);
                if (user != null) {
                    GroupManager manager = new GroupManager();
                    Command addMember = new AddMemberCommand(group, user);
                    manager.setCommand(addMember);
                    manager.executeCommand();
                    updateGroupPanel(group, memberPanel);
                } else {
                    JOptionPane.showMessageDialog(groupFrame, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userName = userNameField.getText();
                User user = findUserByName(userName);
                if (user != null) {
                    GroupManager manager = new GroupManager();
                    Command removeMember = new RemoveMemberCommand(group, user);
                    manager.setCommand(removeMember);
                    manager.executeCommand();
                    updateGroupPanel(group, memberPanel);
                } else {
                    JOptionPane.showMessageDialog(groupFrame, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Alt grupları göstermek için bir panel oluşturma
        JPanel subGroupPanel = new JPanel();
        subGroupPanel.setLayout(new BoxLayout(subGroupPanel, BoxLayout.Y_AXIS));

        for (GroupComponent subGroup : group.getSubGroups()) {
            JLabel subGroupLabel = new JLabel(subGroup.getName());
            subGroupLabel.setForeground(Color.BLUE);
            subGroupLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            subGroupLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        openGroupPage((Group) subGroup, user);
                    }
                }
            });
            subGroupPanel.add(subGroupLabel);
        }

        JScrollPane subGroupScrollPane = new JScrollPane(subGroupPanel);
        subGroupScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, subGroupScrollPane, memberScrollPane);
        splitPane.setDividerLocation(200);

        groupPanel.add(controlPanel, BorderLayout.NORTH);
        groupPanel.add(splitPane, BorderLayout.CENTER);

        groupFrame.add(groupPanel);
        groupFrame.setVisible(true);
    }

    private User findUserByName(String name) {
        for (User user : UserManager.getInstance().getUsers()) {
            if (user.getName().equalsIgnoreCase(name)) {
                return user;
            }
        }
        return null;
    }

    private void updateGroupPanel(Group group, JPanel memberPanel) {
        memberPanel.removeAll();
        for (User member : group.getMembers()) {
            JLabel memberLabel = new JLabel(member.getName());
            memberPanel.add(memberLabel);
        }
        memberPanel.revalidate();
        memberPanel.repaint();
    }
}
