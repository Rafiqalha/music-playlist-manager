import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import java.io.File;                                                                                                                            
import java.util.Random;

public class MusicPlayerFX extends Application {

    private Playlist myPlaylist;
    private SongLibrary myLibrary;
    private SongStack historyStack;
    private Song currentSong;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private Song[] shuffledArray;
    private int totalSongs = 0;
    private int shuffleIndex = 0;
    private Random rand;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private double currentVolume = 0.5;
    private Label lblSongTitle, lblArtist, lblTimeCurrent, lblTimeTotal;
    private ListView<Song> listViewLibrary;
    private Slider songProgressBar, volumeSlider;
    private Button btnPlayPause, btnShuffle, btnRepeat, btnAddSong, btnHistory, btnNext, btnPrev;
    private FontIcon centerAlbumIcon;
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage primaryStage) {
        initBackendData();
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        BorderPane root = new BorderPane();
        root.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        root.getStyleClass().add("root"); // Class CSS utama

        VBox sidebar = new VBox(20);
        sidebar.getStyleClass().add("sidebar-panel");
        sidebar.setPrefWidth(240);
        sidebar.setPadding(new Insets(30, 0, 30, 0));

        HBox logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setPadding(new Insets(0, 25, 20, 25));

        FontIcon logoIcon = new FontIcon("mdi2m-music-circle");
        logoIcon.setIconColor(Color.web("#D4AF37")); // Gold
        logoIcon.setIconSize(22);

        Label lblBrand = new Label("Music Playlist Manager");
        lblBrand.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-letter-spacing: 2px;");

        logoBox.getChildren().addAll(logoIcon, lblBrand);

        VBox menuContainer = new VBox(5);
        Label lblSection = new Label("KOLEKSI SAYA");
        lblSection.setStyle("-fx-text-fill: #444444; -fx-font-size: 10px; -fx-font-weight: bold;");
        lblSection.setPadding(new Insets(20, 0, 5, 25));

        btnAddSong = createSidebarButton("Tambah Lagu", "mdi2f-folder-plus-outline");
        btnHistory = createSidebarButton("Riwayat", "mdi2h-history");

        sidebar.getChildren().addAll(logoBox, new Separator(), menuContainer, lblSection, btnAddSong, btnHistory);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);

        root.setLeft(sidebar);

        VBox centerPanel = new VBox(20);
        centerPanel.setPadding(new Insets(30, 40, 30, 40));
        centerPanel.setAlignment(Pos.TOP_CENTER);

        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        Button btnMin = createMinimalIconBtn("mdi2m-minus");
        btnMin.setOnAction(e -> primaryStage.setIconified(true));
        Button btnClose = createMinimalIconBtn("mdi2c-close");
        btnClose.setOnAction(e -> Platform.exit());
        btnClose.setOnMouseEntered(e -> btnClose.getGraphic().setStyle("-fx-fill: #FF4444;"));
        btnClose.setOnMouseExited(e -> btnClose.getGraphic().setStyle("-fx-fill: #888888;"));
        topBar.getChildren().addAll(btnMin, btnClose);

        HBox contentSplit = new HBox(40);
        VBox.setVgrow(contentSplit, Priority.ALWAYS);

        VBox albumBox = new VBox(20);
        albumBox.setAlignment(Pos.TOP_CENTER);
        albumBox.setPrefWidth(300);

        StackPane artFrame = new StackPane();
        artFrame.getStyleClass().add("album-frame");
        artFrame.setPrefSize(280, 280);
        artFrame.setMaxSize(280, 280);

        centerAlbumIcon = new FontIcon("mdi2m-music-circle-outline");
        centerAlbumIcon.setIconSize(100);
        centerAlbumIcon.setIconColor(Color.web("#333333"));
        artFrame.getChildren().add(centerAlbumIcon);

        VBox infoBox = new VBox(8);
        infoBox.setAlignment(Pos.CENTER);
        lblSongTitle = new Label("No Track Selected");
        lblSongTitle.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        lblSongTitle.setWrapText(true);
        lblSongTitle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        lblArtist = new Label("Unknown Artist");
        lblArtist.setStyle("-fx-text-fill: #D4AF37; -fx-font-size: 14px; -fx-font-weight: normal;");

        infoBox.getChildren().addAll(lblSongTitle, lblArtist);
        albumBox.getChildren().addAll(artFrame, infoBox);

        VBox playlistBox = new VBox(10);
        HBox.setHgrow(playlistBox, Priority.ALWAYS);
        playlistBox.getStyleClass().add("executive-card");
        playlistBox.setPadding(new Insets(20));

        Label lblQueue = new Label("Antrian Lagu");
        lblQueue.setStyle(
                "-fx-text-fill: #666666; -fx-font-size: 11px; -fx-font-weight: bold; -fx-letter-spacing: 1px;");

        listViewLibrary = new ListView<>();
        listViewLibrary.getStyleClass().add("list-view");
        setupListView();
        VBox.setVgrow(listViewLibrary, Priority.ALWAYS);

        playlistBox.getChildren().addAll(lblQueue, listViewLibrary);
        contentSplit.getChildren().addAll(albumBox, playlistBox);
        centerPanel.getChildren().addAll(topBar, contentSplit);
        root.setCenter(centerPanel);

        VBox bottomContainer = new VBox(0);
        bottomContainer.getStyleClass().add("bottom-bar");
        bottomContainer.setPrefHeight(90);
        bottomContainer.setPadding(new Insets(10, 40, 15, 40));

        HBox progressBox = new HBox(15);
        progressBox.setAlignment(Pos.CENTER);
        lblTimeCurrent = new Label("00:00");
        lblTimeCurrent.setStyle("-fx-text-fill: #D4AF37; -fx-font-size: 10px; -fx-font-family: 'Monospaced';");
        songProgressBar = new Slider();
        HBox.setHgrow(songProgressBar, Priority.ALWAYS);
        lblTimeTotal = new Label("00:00");
        lblTimeTotal.setStyle("-fx-text-fill: #666666; -fx-font-size: 10px; -fx-font-family: 'Monospaced';");
        progressBox.getChildren().addAll(lblTimeCurrent, songProgressBar, lblTimeTotal);

        BorderPane controlsRow = new BorderPane();
        controlsRow.setPadding(new Insets(5, 0, 0, 0));

        HBox mainControls = new HBox(25);
        mainControls.setAlignment(Pos.CENTER);
        btnShuffle = createMinimalIconBtn("mdi2s-shuffle-variant");
        btnPrev = createMinimalIconBtn("mdi2s-skip-previous");
        ((FontIcon) btnPrev.getGraphic()).setIconSize(24);
        btnPlayPause = new Button();
        FontIcon playIcon = new FontIcon("mdi2p-play");
        playIcon.setIconSize(24);
        btnPlayPause.setGraphic(playIcon);
        btnPlayPause.getStyleClass().add("play-btn-gold");
        btnNext = createMinimalIconBtn("mdi2s-skip-next");
        ((FontIcon) btnNext.getGraphic()).setIconSize(24);
        btnRepeat = createMinimalIconBtn("mdi2r-repeat");
        mainControls.getChildren().addAll(btnShuffle, btnPrev, btnPlayPause, btnNext, btnRepeat);
        controlsRow.setCenter(mainControls);

        HBox volBox = new HBox(10);
        volBox.setAlignment(Pos.CENTER_RIGHT);
        FontIcon volIcon = new FontIcon("mdi2v-volume-high");
        volIcon.setIconColor(Color.web("#666666"));
        volIcon.setIconSize(16);
        volumeSlider = new Slider(0, 100, 50);
        volumeSlider.setPrefWidth(100);
        volBox.getChildren().addAll(volIcon, volumeSlider);
        controlsRow.setRight(volBox);

        bottomContainer.getChildren().addAll(progressBox, controlsRow);
        root.setBottom(bottomContainer);

        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        setupEvents(primaryStage);

        refreshUIList();
        if (!myPlaylist.isEmpty()) {
            Song firstSong = myPlaylist.play();
            if (firstSong != null) {
                lblSongTitle.setText(firstSong.getTitle());
                lblArtist.setText(firstSong.getArtist());
                listViewLibrary.getSelectionModel().select(firstSong);
                centerAlbumIcon.setIconColor(Color.web("#D4AF37"));
                try {
                    File f = new File(firstSong.getFilePath());
                    mediaPlayer = new MediaPlayer(new Media(f.toURI().toString()));
                    mediaPlayer.setVolume(currentVolume);
                    mediaPlayer.setOnReady(() -> {
                        songProgressBar.setMax(mediaPlayer.getTotalDuration().toSeconds());
                        lblTimeTotal.setText(formatTime(mediaPlayer.getTotalDuration()));
                    });
                    mediaPlayer.currentTimeProperty().addListener((o, old, now) -> {
                        if (!songProgressBar.isPressed())
                            songProgressBar.setValue(now.toSeconds());
                        lblTimeCurrent.setText(formatTime(now));
                    });
                    mediaPlayer.setOnEndOfMedia(() -> {
                        if (isRepeat) {
                            mediaPlayer.seek(Duration.ZERO);
                            mediaPlayer.play();
                        } else
                            playNextSong();
                    });
                } catch (Exception e) {
                }
            }
        }

        Scene scene = new Scene(root, 1200, 750);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setTitle("Music Playlist Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createSidebarButton(String text, String code) {
        Button btn = new Button(text);
        FontIcon icon = new FontIcon(code);
        icon.setIconSize(16);
        icon.setIconColor(Color.web("#666666")); // Default Grey
        btn.setGraphic(icon);
        btn.setGraphicTextGap(15);
        btn.getStyleClass().add("sidebar-btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.hoverProperty().addListener((obs, old, isHover) -> {
            if (isHover)
                icon.setIconColor(Color.web("#D4AF37"));
            else
                icon.setIconColor(Color.web("#666666"));
        });

        return btn;
    }

    private Button createMinimalIconBtn(String code) {
        Button btn = new Button();
        FontIcon icon = new FontIcon(code);
        icon.setIconColor(Color.web("#888888"));
        icon.setIconSize(18);
        btn.setGraphic(icon);
        btn.getStyleClass().add("icon-btn-minimal");
        btn.hoverProperty().addListener((obs, old, isHover) -> {
            if (isHover)
                icon.setIconColor(Color.web("#D4AF37"));
            else {
                if (code.contains("shuffle") && isShuffle)
                    icon.setIconColor(Color.web("#D4AF37"));
                else if (code.contains("repeat") && isRepeat)
                    icon.setIconColor(Color.web("#D4AF37"));
                else
                    icon.setIconColor(Color.web("#888888"));
            }
        });
        return btn;
    }

    private void updateButtonStyle(Button btn, boolean isActive) {
        FontIcon icon = (FontIcon) btn.getGraphic();
        if (isActive)
            icon.setIconColor(Color.web("#D4AF37"));
        else
            icon.setIconColor(Color.web("#888888"));
    }

    private void updatePlayButtonIcon() {
        FontIcon icon = (FontIcon) btnPlayPause.getGraphic();
        icon.setIconLiteral(isPlaying ? "mdi2p-pause" : "mdi2p-play");
    }

    private void setupListView() {
        listViewLibrary.setCellFactory(param -> new ListCell<Song>() {
            @Override
            protected void updateItem(Song item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    HBox row = new HBox(15);
                    row.setAlignment(Pos.CENTER_LEFT);

                    FontIcon noteIcon = new FontIcon("mdi2m-music-note");
                    noteIcon.setIconColor(Color.web("#333333"));

                    VBox textContainer = new VBox(3);
                    Label title = new Label(item.getTitle());
                    title.setStyle("-fx-text-fill: #E0E0E0; -fx-font-weight: bold; -fx-font-size: 13px;");

                    Label artist = new Label(item.getArtist());
                    artist.setStyle("-fx-text-fill: #666666; -fx-font-size: 11px;");

                    textContainer.getChildren().addAll(title, artist);

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Button btnDel = new Button();
                    FontIcon delIcon = new FontIcon("mdi2c-close");
                    delIcon.setIconColor(Color.web("#333333"));
                    delIcon.setIconSize(14);
                    btnDel.setGraphic(delIcon);
                    btnDel.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                    btnDel.setOnMouseEntered(e -> delIcon.setIconColor(Color.web("#FF4444")));
                    btnDel.setOnMouseExited(e -> delIcon.setIconColor(Color.web("#333333")));
                    btnDel.setOnMouseClicked(e -> {
                        deleteSong(item);
                        e.consume();
                    });
                    row.getChildren().addAll(noteIcon, textContainer, spacer, btnDel);
                    setGraphic(row);
                }
            }
        });

        listViewLibrary.setOnMouseClicked(e -> {
            Song selected = listViewLibrary.getSelectionModel().getSelectedItem();
            if (selected != null) {
                myPlaylist.setCurrentSong(selected);
                playSongFile(selected);
            }
        });
    }

    private void setupEvents(Stage stage) {
        songProgressBar.setOnMousePressed(e -> {
            if (mediaPlayer != null)
                mediaPlayer.pause();
        });
        songProgressBar.setOnMouseReleased(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(Duration.seconds(songProgressBar.getValue()));
                mediaPlayer.play();
                isPlaying = true;
                updatePlayButtonIcon();
            }
        });
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentVolume = newVal.doubleValue() / 100.0;
            if (mediaPlayer != null)
                mediaPlayer.setVolume(currentVolume);
        });
        btnPlayPause.setOnAction(e -> togglePlayPause());
        btnNext.setOnAction(e -> playNextSong());
        btnPrev.setOnAction(e -> playPrevSong());
        btnShuffle.setOnAction(e -> {
            isShuffle = !isShuffle;
            updateButtonStyle(btnShuffle, isShuffle);
            if (isShuffle) {
                buildShuffledArray();
                shuffleIndex = -1;
            }
        });
        btnRepeat.setOnAction(e -> {
            isRepeat = !isRepeat;
            updateButtonStyle(btnRepeat, isRepeat);
        });
        btnAddSong.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));
            File f = fc.showOpenDialog(stage);
            if (f != null) {
                String raw = f.getName().replace(".mp3", "");
                String t = raw, a = "Unknown Artist";
                if (raw.contains("-")) {
                    String[] p = raw.split("-", 2);
                    a = p[0].trim();
                    t = p[1].trim();
                } else if (raw.contains("_")) {
                    String[] p = raw.split("_", 2);
                    a = p[0].trim();
                    t = p[1].trim();
                }
                t = fixCamelCase(t).replace("_", " ").trim();
                a = fixCamelCase(a).replace("_", " ").trim();
                Song s = new Song(t, a, f.getAbsolutePath());
                myLibrary.insert(s);
                myPlaylist.addSong(s);
                SongStorage.savePlaylist(myPlaylist);
                refreshUIList();
                buildShuffledArray();
            }
        });
        btnHistory.setOnAction(e -> showHistory());
    }

    private void deleteSong(Song s) {
        if (s == null)
            return;
        if (currentSong != null && currentSong.equals(s)) {
            if (mediaPlayer != null)
                mediaPlayer.stop();
            lblSongTitle.setText("Stopped");
            lblArtist.setText("-");
            songProgressBar.setValue(0);
            isPlaying = false;
            updatePlayButtonIcon();
        }
        listViewLibrary.getItems().remove(s);
        Playlist newP = new Playlist();
        for (Song x : listViewLibrary.getItems())
            newP.addSong(x);
        myPlaylist = newP;
        SongStorage.savePlaylist(myPlaylist);
        buildShuffledArray();
    }

    private void playSongFile(Song s) {
        if (s == null)
            return;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        if (historyStack.isEmpty() || historyStack.peek() != s)
            historyStack.push(s);
        lblSongTitle.setText(s.getTitle());
        lblArtist.setText(s.getArtist());
        listViewLibrary.getSelectionModel().select(s);
        centerAlbumIcon.setIconColor(Color.web("#D4AF37"));

        try {
            File f = new File(s.getFilePath());
            Media m = new Media(f.toURI().toString());
            mediaPlayer = new MediaPlayer(m);
            mediaPlayer.setVolume(currentVolume);
            mediaPlayer.setOnReady(() -> {
                songProgressBar.setMax(m.getDuration().toSeconds());
                isPlaying = true;
                mediaPlayer.play();
                updatePlayButtonIcon();
                lblTimeTotal.setText(formatTime(m.getDuration()));
            });
            mediaPlayer.currentTimeProperty().addListener((o, old, now) -> {
                if (!songProgressBar.isPressed())
                    songProgressBar.setValue(now.toSeconds());
                lblTimeCurrent.setText(formatTime(now));
            });
            mediaPlayer.setOnEndOfMedia(() -> {
                if (isRepeat) {
                    mediaPlayer.seek(Duration.ZERO);
                    mediaPlayer.play();
                } else
                    playNextSong();
            });
        } catch (Exception e) {
        }
    }

    private void togglePlayPause() {
        if (mediaPlayer == null) {
            if (currentSong == null)
                currentSong = myPlaylist.play();
            playSongFile(currentSong);
            return;
        }
        if (isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
        } else {
            mediaPlayer.play();
            isPlaying = true;
        }
        updatePlayButtonIcon();
    }

    private void buildShuffledArray() {
        int c = 0;
        SongNode curr = myPlaylist.getHead();
        while (curr != null) {
            c++;
            curr = curr.getNext();
        }
        totalSongs = c;
        shuffledArray = new Song[c];
        curr = myPlaylist.getHead();
        int i = 0;
        while (curr != null) {
            shuffledArray[i++] = curr.getData();
            curr = curr.getNext();
        }
        for (int k = c - 1; k > 0; k--) {
            int j = rand.nextInt(k + 1);
            Song temp = shuffledArray[k];
            shuffledArray[k] = shuffledArray[j];
            shuffledArray[j] = temp;
        }
    }

    private void playNextSong() {
        if (isShuffle) {
            shuffleIndex++;
            if (shuffleIndex >= totalSongs) {
                shuffleIndex = 0;
                buildShuffledArray();
            }
            currentSong = shuffledArray[shuffleIndex];
        } else {
            currentSong = myPlaylist.next();
        }
        playSongFile(currentSong);
    }

    private void playPrevSong() {
        if (isShuffle) {
            shuffleIndex--;
            if (shuffleIndex < 0)
                shuffleIndex = totalSongs - 1;
            currentSong = shuffledArray[shuffleIndex];
        } else {
            currentSong = myPlaylist.previous();
        }
        playSongFile(currentSong);
    }

    private String formatTime(Duration d) {
        if (d == null)
            return "00:00";
        int s = (int) d.toSeconds();
        return String.format("%02d:%02d", s / 60, s % 60);
    }

    private String fixCamelCase(String s) {
        if (s == null)
            return "";
        return s.replaceAll("(?<=[a-z])(?=[A-Z])", " ");
    }

    private void showHistory() {
        SongStack tempStack = new SongStack(100);
        javafx.collections.ObservableList<String> historyItems = javafx.collections.FXCollections.observableArrayList();

        if (historyStack.isEmpty()) {
            historyItems.add("No playback history yet.");
        } else {
            while (!historyStack.isEmpty()) {
                Song s = historyStack.pop();
                historyItems.add(s.getTitle() + " - " + s.getArtist());
                tempStack.push(s);
            }
            while (!tempStack.isEmpty()) {
                historyStack.push(tempStack.pop());
            }
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("History");
        dialog.initStyle(StageStyle.TRANSPARENT);

        Label lblTitle = new Label("RECENTLY PLAYED");
        lblTitle.getStyleClass().add("history-title");

        ListView<String> listView = new ListView<>(historyItems);
        listView.getStyleClass().add("history-list");
        listView.setPrefHeight(300);
        listView.setPrefWidth(400);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: transparent;");
        content.getChildren().addAll(lblTitle, listView);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(content);
        dialogPane.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-dialog-pane");

        ButtonType closeType = new ButtonType("CLOSE", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().add(closeType);

        Button closeBtn = (Button) dialogPane.lookupButton(closeType);
        closeBtn.getStyleClass().add("dialog-btn");

        dialog.setOnShowing(e -> {
            Scene scene = dialogPane.getScene();
            if (scene != null) {
                scene.setFill(Color.TRANSPARENT);
                if (scene.getWindow() instanceof Stage) {
                    ((Stage) scene.getWindow()).initStyle(StageStyle.TRANSPARENT);
                }
            }
        });

        final double[] xOffset = { 0 };
        final double[] yOffset = { 0 };
        dialogPane.setOnMousePressed(event -> {
            xOffset[0] = event.getSceneX();
            yOffset[0] = event.getSceneY();
        });
        dialogPane.setOnMouseDragged(event -> {
            dialog.setX(event.getScreenX() - xOffset[0]);
            dialog.setY(event.getScreenY() - yOffset[0]);
        });

        dialog.showAndWait();
    }

    private void initBackendData() {
        myLibrary = new SongLibrary(100);
        myPlaylist = new Playlist();
        historyStack = new SongStack(50);
        rand = new Random();
        Song[] l = SongStorage.loadSongs();
        if (l != null && l.length > 0) {
            for (Song s : l) {
                myLibrary.insert(s);
                myPlaylist.addSong(s);
            }
        }
        buildShuffledArray();
        currentSong = myPlaylist.play();
    }

    private void refreshUIList() {
        listViewLibrary.getItems().clear();
        SongNode curr = myPlaylist.getHead();
        while (curr != null) {
            listViewLibrary.getItems().add(curr.getData());
            curr = curr.getNext();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}