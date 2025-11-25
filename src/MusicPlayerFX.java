import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.Random;

public class MusicPlayerFX extends Application {

    // --- TEMA: MODERN LIGHT (Blue Accent) ---
    private final String THEME_BG_MAIN = "#FFFFFF";
    private final String THEME_BG_SIDEBAR = "#F8F9FA";
    private final String THEME_ACCENT = "#3D5AFE";
    private final String THEME_ACCENT_LIGHT = "#E8EAF6";
    private final String THEME_TEXT_MAIN = "#212121";
    private final String THEME_TEXT_SEC = "#757575";

    // --- STRUKTUR DATA ---
    private Playlist myPlaylist;
    private SongLibrary myLibrary;
    private SongStack historyStack;
    private Song currentSong;

    // Logic
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private Song[] shuffledArray;
    private int totalSongs = 0;
    private int shuffleIndex = 0;
    private Random rand;

    // Player
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private double currentVolume = 0.5;

    // UI Components
    private Label lblSongTitle, lblArtist, lblTimeCurrent, lblTimeTotal;
    private ListView<Song> listViewLibrary;
    private Slider songProgressBar, volumeSlider;
    private Button btnPlayPause, btnShuffle, btnRepeat, btnAddSong, btnNext, btnPrev, btnHistory;
    private Image imgPlayIcon, imgPauseIcon, imgDeleteIcon;
    private ImageView vinylView;

    @Override
    public void start(Stage primaryStage) {
        loadAssets(); // Load gambar dari src/assets
        initBackendData();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + THEME_BG_MAIN + ";");

        // ==========================================================
        // A. SIDEBAR
        // ==========================================================
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(25));
        sidebar.setPrefWidth(320);
        sidebar.setStyle("-fx-background-color: " + THEME_BG_SIDEBAR
                + "; -fx-border-color: #E0E0E0; -fx-border-width: 0 1 0 0;");

        Label lblLibHeader = new Label("LIBRARY");
        lblLibHeader.setTextFill(Color.web(THEME_ACCENT));
        lblLibHeader.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        listViewLibrary = new ListView<>();
        listViewLibrary.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent;");

        // Custom Cell Factory
        listViewLibrary.setCellFactory(param -> new ListCell<Song>() {
            @Override
            protected void updateItem(Song item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    HBox rowBox = new HBox(10);
                    rowBox.setAlignment(Pos.CENTER_LEFT);

                    VBox textBox = new VBox(4);
                    Label title = new Label(item.getTitle());
                    Label artist = new Label(item.getArtist());
                    title.setTextFill(Color.web(THEME_TEXT_MAIN));
                    title.setFont(Font.font("Arial", FontWeight.BOLD, 13));
                    artist.setTextFill(Color.web(THEME_TEXT_SEC));
                    artist.setFont(Font.font("Arial", 11));
                    textBox.getChildren().addAll(title, artist);

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Button btnDel = new Button();
                    // Gunakan image yang sudah di-load di memori
                    ImageView delIcon = new ImageView(imgDeleteIcon);
                    delIcon.setFitWidth(16);
                    delIcon.setFitHeight(16);
                    ColorAdjust redTint = new ColorAdjust();
                    redTint.setSaturation(1.0);

                    btnDel.setGraphic(delIcon);
                    btnDel.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                    btnDel.setTooltip(new Tooltip("Hapus Lagu"));
                    btnDel.setOnMouseEntered(
                            e -> btnDel.setStyle("-fx-background-color: #FFEBEE; -fx-background-radius: 50%;"));
                    btnDel.setOnMouseExited(e -> btnDel.setStyle("-fx-background-color: transparent;"));
                    btnDel.setOnAction(e -> deleteSong(item));

                    rowBox.getChildren().addAll(textBox, spacer, btnDel);
                    setGraphic(rowBox);

                    if (isSelected()) {
                        title.setTextFill(Color.web(THEME_ACCENT));
                        setStyle(
                                "-fx-background-color: white; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5,0,0,2);");
                    } else {
                        setStyle("-fx-background-color: transparent;");
                    }
                }
            }
        });

        VBox.setVgrow(listViewLibrary, Priority.ALWAYS);
        refreshUIList();

        listViewLibrary.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Song selected = listViewLibrary.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    myPlaylist.setCurrentSong(selected);
                    playSongFile(selected);
                }
            }
        });

        HBox sidebarControls = new HBox(15);
        // Panggil asset dari resource stream
        btnAddSong = createSideButton("Add Music", "/assets/add.png");
        btnHistory = createSideButton("History", "/assets/history.png");
        sidebarControls.getChildren().addAll(btnAddSong, btnHistory);

        sidebar.getChildren().addAll(lblLibHeader, listViewLibrary, sidebarControls);
        root.setLeft(sidebar);

        // ==========================================================
        // B. CENTER
        // ==========================================================
        VBox centerPane = new VBox(25);
        centerPane.setAlignment(Pos.CENTER);
        centerPane.setPadding(new Insets(30));

        vinylView = loadIcon("/assets/vinylRecord.png", 260); // Coba PNG
        if (vinylView.getImage() == null || vinylView.getImage().isError()) {
            vinylView = loadIcon("/assets/vinylRecord.gif", 260); // Coba gif (kecil)
        }
        if (vinylView.getImage() == null || vinylView.getImage().isError()) {
            vinylView = loadIcon("/assets/VinylRecord.gif", 260); // Coba V besar
        }
        if (vinylView.getImage() == null || vinylView.getImage().isError()) {
            vinylView = loadIcon("/assets/vinylRecord.GIF", 260); // Coba GIF besar
        }

        DropShadow shadow = new DropShadow(25, Color.rgb(0, 0, 0, 0.15));
        vinylView.setEffect(shadow);

        lblSongTitle = new Label("JavaFX Pure Player");
        lblSongTitle.setTextFill(Color.web(THEME_TEXT_MAIN));
        lblSongTitle.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        lblArtist = new Label("Select a song");
        lblArtist.setTextFill(Color.web(THEME_TEXT_SEC));
        lblArtist.setFont(Font.font("Arial", 16));

        centerPane.getChildren().addAll(vinylView, lblSongTitle, lblArtist);
        root.setCenter(centerPane);

        // ==========================================================
        // C. BOTTOM PLAYER
        // ==========================================================
        VBox bottomContainer = new VBox(10);
        bottomContainer.setPadding(new Insets(15, 30, 20, 30));
        bottomContainer.setStyle(
                "-fx-background-color: " + THEME_BG_MAIN + "; -fx-border-color: #E0E0E0; -fx-border-width: 1 0 0 0;");

        HBox progressBox = new HBox(15);
        progressBox.setAlignment(Pos.CENTER);
        lblTimeCurrent = new Label("00:00");
        lblTimeCurrent.setTextFill(Color.web(THEME_ACCENT));
        lblTimeCurrent.setFont(Font.font("Monospaced", FontWeight.BOLD, 12));
        lblTimeTotal = new Label("00:00");
        lblTimeTotal.setTextFill(Color.web(THEME_TEXT_SEC));
        lblTimeTotal.setFont(Font.font("Monospaced", FontWeight.BOLD, 12));
        songProgressBar = new Slider();
        HBox.setHgrow(songProgressBar, Priority.ALWAYS);
        songProgressBar
                .setStyle("-fx-control-inner-background: #E0E0E0; -fx-accent: " + THEME_ACCENT + "; -fx-cursor: hand;");
        progressBox.getChildren().addAll(lblTimeCurrent, songProgressBar, lblTimeTotal);

        BorderPane controlRow = new BorderPane();
        HBox navButtons = new HBox(20);
        navButtons.setAlignment(Pos.CENTER);

        // Panggil asset dari resource stream
        btnShuffle = createIconButton("/assets/shuffle.png", 18);
        btnPrev = createIconButton("/assets/prev.png", 24);
        btnPlayPause = createPlayButton();
        btnNext = createIconButton("/assets/next.png", 24);
        btnRepeat = createIconButton("/assets/repeat.png", 18);

        navButtons.getChildren().addAll(btnShuffle, btnPrev, btnPlayPause, btnNext, btnRepeat);
        controlRow.setCenter(navButtons);

        HBox volBox = new HBox(10);
        volBox.setAlignment(Pos.CENTER_RIGHT);
        Label lblVol = new Label("Vol");
        lblVol.setTextFill(Color.web(THEME_TEXT_SEC));
        lblVol.setFont(Font.font("Arial", 11));
        volumeSlider = new Slider(0, 100, 50);
        volumeSlider.setPrefWidth(100);
        volumeSlider.setStyle(
                "-fx-control-inner-background: #E0E0E0; -fx-accent: " + THEME_TEXT_SEC + "; -fx-cursor: hand;");
        volBox.getChildren().addAll(lblVol, volumeSlider);
        controlRow.setRight(volBox);

        bottomContainer.getChildren().addAll(progressBox, controlRow);
        root.setBottom(bottomContainer);

        // ==========================================================
        // D. EVENTS
        // ==========================================================

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
        btnHistory.setOnAction(e -> showHistory());

        btnAddSong.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));
            File f = fc.showOpenDialog(primaryStage);
            if (f != null) {
                String raw = f.getName().replace(".mp3", "");
                String t = raw, a = "Unknown Artist";
                if (raw.contains("-")) {
                    String[] p = raw.split("-", 2);
                    t = p[1].trim();
                    a = p[0].trim();
                } // Nama - Judul
                else if (raw.contains("_")) {
                    String[] p = raw.split("_", 2);
                    t = p[1].trim();
                    a = p[0].trim();
                }

                t = fixCamelCase(t);
                a = fixCamelCase(a);
                t = t.replace("_", " ").trim();
                a = a.replace("_", " ").trim();

                Song s = new Song(t, a, f.getAbsolutePath());
                myLibrary.insert(s);
                myPlaylist.addSong(s);
                SongStorage.savePlaylist(myPlaylist);
                refreshUIList();
                buildShuffledArray();
            }
        });

        Scene scene = new Scene(root, 1050, 720);
        primaryStage.setTitle("JavaFX Modern Player");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ==========================================================
    // LOGIC & HELPERS (RESOURCE STREAM VERSION)
    // ==========================================================

    // 1. Load Assets Utama (Play, Pause, Delete) dari src/assets
    private void loadAssets() {
        try {
            // PENTING: Gunakan getResourceAsStream
            imgPlayIcon = new Image(getClass().getResourceAsStream("/assets/play.png"));
            imgPauseIcon = new Image(getClass().getResourceAsStream("/assets/pause.png"));
            imgDeleteIcon = new Image(getClass().getResourceAsStream("/assets/delete.png"));
        } catch (Exception e) {
            System.out.println("Gagal memuat aset gambar utama. Pastikan folder src/assets ada.");
        }
    }

    // 2. Helper Load Icon Satuan (Untuk tombol Shuffle, Next, dll)
    private ImageView loadIcon(String path, int size) {
        try {
            // Path harus diawali dengan "/" (contoh: "/assets/next.png")
            if (!path.startsWith("/"))
                path = "/" + path;

            Image img = new Image(getClass().getResourceAsStream(path));
            ImageView v = new ImageView(img);
            v.setFitWidth(size);
            v.setFitHeight(size);
            return v;
        } catch (Exception e) {
            // Jika gagal, return icon kosong agar tidak error
            return new ImageView();
        }
    }

    // Update icon Play/Pause
    private void updatePlayButtonIcon() {
        ImageView icon = (ImageView) btnPlayPause.getGraphic();
        // Gunakan variabel image yang sudah di-load di awal
        icon.setImage(isPlaying ? imgPauseIcon : imgPlayIcon);

        ColorAdjust bright = new ColorAdjust();
        bright.setBrightness(1.0);
        icon.setEffect(bright);
        icon.setFitWidth(22);
        icon.setFitHeight(22);
    }

    private void updateButtonStyle(Button btn, boolean isActive) {
        ImageView icon = (ImageView) btn.getGraphic();
        if (isActive) {
            btn.setStyle("-fx-background-color: " + THEME_ACCENT_LIGHT + "; -fx-background-radius: 50%;");
            icon.setOpacity(1.0);
        } else {
            btn.setStyle("-fx-background-color: transparent;");
            icon.setOpacity(0.6);
            icon.setEffect(null);
        }
    }

    private Button createPlayButton() {
        Button btn = new Button();
        // Gunakan imgPlayIcon yang sudah di-load
        ImageView icon = new ImageView(imgPlayIcon);
        icon.setFitWidth(22);
        icon.setFitHeight(22);
        ColorAdjust bright = new ColorAdjust();
        bright.setBrightness(1.0);
        icon.setEffect(bright);
        btn.setGraphic(icon);
        btn.setStyle("-fx-background-color: " + THEME_ACCENT
                + "; -fx-background-radius: 100; -fx-min-width: 60px; -fx-min-height: 60px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(61, 90, 254, 0.4), 15, 0, 0, 4);");
        btn.setOnMouseEntered(e -> {
            btn.setScaleX(1.1);
            btn.setScaleY(1.1);
        });
        btn.setOnMouseExited(e -> {
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
        });
        return btn;
    }

    private Button createIconButton(String path, int size) {
        Button btn = new Button();
        ImageView icon = loadIcon(path, size); // Panggil helper baru
        ColorAdjust dark = new ColorAdjust();
        dark.setBrightness(-1.0);
        icon.setEffect(dark);
        icon.setOpacity(0.6);
        btn.setGraphic(icon);
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> {
            icon.setOpacity(1.0);
            btn.setStyle("-fx-background-color: #F0F0F0; -fx-background-radius: 50%;");
        });
        btn.setOnMouseExited(e -> {
            if ((!btn.getText().equals("Shuffle") && !isShuffle) && (!btn.getText().equals("Repeat") && !isRepeat)) {
                icon.setOpacity(0.6);
                btn.setStyle("-fx-background-color: transparent;");
            }
        });
        return btn;
    }

    private Button createSideButton(String text, String iconPath) {
        Button btn = new Button(text);
        ImageView icon = loadIcon(iconPath, 16); // Panggil helper baru
        ColorAdjust dark = new ColorAdjust();
        dark.setBrightness(-0.8);
        icon.setEffect(dark);
        btn.setGraphic(icon);
        btn.setGraphicTextGap(15);
        btn.setTextFill(Color.web(THEME_TEXT_SEC));
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        btn.setStyle("-fx-background-color: transparent; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnMouseEntered(e -> {
            btn.setTextFill(Color.web(THEME_ACCENT));
            btn.setStyle(
                    "-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5,0,0,2); -fx-alignment: CENTER_LEFT; -fx-background-radius: 8px;");
        });
        btn.setOnMouseExited(e -> {
            btn.setTextFill(Color.web(THEME_TEXT_SEC));
            btn.setStyle("-fx-background-color: transparent; -fx-alignment: CENTER_LEFT;");
        });
        return btn;
    }

    // --- LOGIC LAINNYA (SAMA) ---
    private void deleteSong(Song songToDelete) {
        if (songToDelete == null)
            return;
        if (currentSong != null && currentSong.equals(songToDelete)) {
            if (mediaPlayer != null)
                mediaPlayer.stop();
            lblSongTitle.setText("Stopped");
            lblArtist.setText("-");
            songProgressBar.setValue(0);
            isPlaying = false;
            updatePlayButtonIcon();
        }
        listViewLibrary.getItems().remove(songToDelete);
        Playlist newPlaylist = new Playlist();
        for (Song s : listViewLibrary.getItems())
            newPlaylist.addSong(s);
        myPlaylist = newPlaylist;
        SongStorage.savePlaylist(myPlaylist);
        buildShuffledArray();
    }

    private void playSongFile(Song song) {
        if (song == null)
            return;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        if (historyStack.isEmpty() || historyStack.peek() != song)
            historyStack.push(song);
        lblSongTitle.setText(song.getTitle());
        lblArtist.setText(song.getArtist());
        listViewLibrary.getSelectionModel().select(song);
        try {
            File file = new File(song.getFilePath());
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(currentVolume);
            mediaPlayer.setOnReady(() -> {
                songProgressBar.setMax(media.getDuration().toSeconds());
                isPlaying = true;
                mediaPlayer.play();
                updatePlayButtonIcon();
                lblTimeTotal.setText(formatTime(media.getDuration()));
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
        int count = 0;
        SongNode curr = myPlaylist.getHead();
        while (curr != null) {
            count++;
            curr = curr.getNext();
        }
        totalSongs = count;
        shuffledArray = new Song[count];
        curr = myPlaylist.getHead();
        int idx = 0;
        while (curr != null) {
            shuffledArray[idx++] = curr.getData();
            curr = curr.getNext();
        }
        for (int i = count - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            Song temp = shuffledArray[i];
            shuffledArray[i] = shuffledArray[j];
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

    private void showHistory() {
        StringBuilder sb = new StringBuilder("Riwayat:\n");
        SongStack temp = new SongStack(50);
        while (!historyStack.isEmpty()) {
            Song s = historyStack.pop();
            sb.append("- ").append(s.getTitle()).append("\n");
            temp.push(s);
        }
        while (!temp.isEmpty())
            historyStack.push(temp.pop());
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("History");
        a.setHeaderText(null);
        a.setContentText(sb.toString());
        a.showAndWait();
    }

    private String fixCamelCase(String text) {
        if (text == null)
            return "";
        return text.replaceAll("(?<=[a-z])(?=[A-Z])", " ");
    }

    private void initBackendData() {
        myLibrary = new SongLibrary(100);
        myPlaylist = new Playlist();
        historyStack = new SongStack(50);
        rand = new Random();
        Song[] loadedData = SongStorage.loadSongs();
        if (loadedData != null && loadedData.length > 0) {
            for (Song s : loadedData) {
                myLibrary.insert(s);
                myPlaylist.addSong(s);
            }
        }
        buildShuffledArray();
        currentSong = myPlaylist.play();
    }

    private void refreshUIList() {
        listViewLibrary.getItems().clear();
        SongNode current = myPlaylist.getHead();
        while (current != null) {
            listViewLibrary.getItems().add(current.getData());
            current = current.getNext();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}