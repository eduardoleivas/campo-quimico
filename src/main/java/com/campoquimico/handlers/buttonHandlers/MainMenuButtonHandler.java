package com.campoquimico.handlers.buttonHandlers;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.campoquimico.database.DatabaseReader;
import com.campoquimico.game.GameScreen;
import com.campoquimico.handlers.gameHandlers.GameHandler;
import com.campoquimico.handlers.gameHandlers.TutorialHandler;
import com.campoquimico.handlers.optionsHandlers.OptionsHandler;
import com.campoquimico.handlers.resourceHandlers.ResourceHandler;
import com.campoquimico.objects.GuideItem;

public class MainMenuButtonHandler {

    private final Stage primaryStage;
    private DatabaseReader dbReader;
    private ListView<GuideItem> listView;
    ResourceHandler resourceHandler = new ResourceHandler();

    public MainMenuButtonHandler(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    //START GAME HANDLER
    public void handleStartButton(ActionEvent event) {
        dbReader = new DatabaseReader(OptionsHandler.getInstance().getDatabase());
        int moleculeId;
        if (OptionsHandler.getInstance().isRandomMode()) {
            moleculeId = dbReader.getRandomMolecule();
        } else {
            moleculeId = GameHandler.getInstance().getSequentialId();
        }
    
        int gamemode = dbReader.getMoleculeGamemode(moleculeId);
        GameHandler.getInstance().setGamemode(gamemode);
        String[][] molecule = dbReader.processMolecule(moleculeId);
        Stage boardStage = new Stage();
        GameScreen gameScreen = new GameScreen(molecule, dbReader.getMoleculeName(moleculeId), boardStage, primaryStage, moleculeId);
        GameHandler.getInstance().setGameScreen(gameScreen);
        boardStage.setTitle("JOGO");
        boardStage.setScene(gameScreen.getGameScreen());
        boardStage.show();
    
        if (primaryStage.isShowing()) {
            primaryStage.hide();
        }
    
        boardStage.setOnCloseRequest(closeEvent -> gameScreen.onCloseGame(closeEvent, primaryStage));
    }

    //SETTINGS BUTTON HANDLER
    public void handleSettingsButton(ActionEvent event) {
        Stage settingsStage = new Stage();
        settingsStage.setTitle("Configurações");

        //VOLUME SLIDER
        Label volumeLabel = new Label("Volume");
        Slider volumeSlider = new Slider(0, 1.0, 5); // Min = 0, Max = 1, Default = 0.5
        volumeSlider.setValue(OptionsHandler.getInstance().getVolume());
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setMajorTickUnit(0.1);
        volumeSlider.setBlockIncrement(0.1);
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> 
            OptionsHandler.getInstance().setVolume(newVal.doubleValue()));

        //RANDOM MODE CHECKBOX
        CheckBox randomModeCheckBox = new CheckBox("Modo Aleatório: Ativado");
        randomModeCheckBox.setSelected(true);
        randomModeCheckBox.setOnAction(e -> {
            String status = randomModeCheckBox.isSelected() ? "Ativado" : "Desativado";
            randomModeCheckBox.setText("Modo Aleatório: " + status);
            OptionsHandler.getInstance().setRandomMode(randomModeCheckBox.isSelected());
        });

        //FILE PICKER BUTTON + RESET BUTTON
        Button selectDatabaseButton = new Button("Selecionar Banco de Dados");
        Button resetDatabaseButton = new Button("Redefinir");
        Label filePathLabel = new Label("Nenhum arquivo selecionado");

        selectDatabaseButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Escolha um arquivo .xlsx");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
            File selectedFile = fileChooser.showOpenDialog(settingsStage);
            if (selectedFile != null) {
                filePathLabel.setText(selectedFile.getAbsolutePath());
                OptionsHandler.getInstance().setDatabase(selectedFile.getAbsolutePath());
            }
        });

        //RESET DATABASE BUTTON
        resetDatabaseButton.setOnAction(e -> {
            OptionsHandler.getInstance().resetDatabase();
            filePathLabel.setText("Nenhum arquivo selecionado");
        });

        //HBOX FOR BUTTONS
        HBox databaseButtonsBox = new HBox(10, selectDatabaseButton, resetDatabaseButton);
        databaseButtonsBox.setAlignment(Pos.CENTER);

        //OK BUTTON (CLOSE WINDOW)
        Button okButton = new Button("OK");
        okButton.setOnAction(e -> settingsStage.close());
        if(filePathLabel.getText().equals("Nenhum arquivo selecionado")) {
            OptionsHandler.getInstance().resetDatabase();
        }

        //LAYOUT
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(
            volumeLabel, volumeSlider, 
            randomModeCheckBox, 
            databaseButtonsBox, filePathLabel, 
            okButton
        );

        //SCENE SETUP
        Scene scene = new Scene(layout, 350, 300);
        settingsStage.setScene(scene);
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.showAndWait();
    }

    public void handleHowToPlayButton(ActionEvent event) {
        Stage guia = new Stage();
        guia.setTitle("Guia Interativo");

        //LIST VIEW OF THE CLICKABLE ITEMS
        listView = new ListView<>();
        listView.getItems().addAll(
            new GuideItem(1, "1 - MENU PRINCIPAL"),
            new GuideItem(2, "  1.1 - OS BOTÕES"),
            new GuideItem(3, "2 - CONFIGURAÇÕES"),
            new GuideItem(4, "  2.1 - VOLUME"),
            new GuideItem(5, "  2.2 - SELETOR DE MODO"),
            new GuideItem(6, "    2.2.1 - MODO ALEATÓRIO"),
            new GuideItem(7, "    2.2.2 - MODO LINEAR"),
            new GuideItem(8, "  2.3 - SELETOR DE BANCO DE DADOS"),
            new GuideItem(9, "  2.4 - REINICIAR BANCO DE DADOS"),
            new GuideItem(10, "  2.5 - CONCLUIR"),
            new GuideItem(11, "3 - INICIANDO UMA PARTIDA"),
            new GuideItem(12, "  3.1 - O TABULEIRO"),
            new GuideItem(13, "  3.2 - OS BLOCOS"),
            new GuideItem(14, "  3.3 - O PRIMEIRO CLIQUE"),
            new GuideItem(15, "    3.3.1 - ABRINDO BLOCOS"),
            new GuideItem(16, "      3.3.1.1 - BLOCOS PREENCHIDOS"),
            new GuideItem(17, "      3.3.1.2 - BLOCOS VAZIOS"),
            new GuideItem(18, "  3.4 - POSICIONAMENTO E ROTAÇÃO DA MOLÉCULA"),
            new GuideItem(19, "  3.5 - DICAS"),
            new GuideItem(20, "  3.6 - MULTI-DICAS"),
            new GuideItem(21, "  3.7 - ADIVINHAÇÃO"),
            new GuideItem(22, "    3.7.1 - RESPOSTA ERRADA"),
            new GuideItem(23, "    3.7.2 - RESPOSTA CORRETA"),
            new GuideItem(24, "  3.8 - FIM DE PARTIDA LINEAR"),
            new GuideItem(25, "  3.9 - PÓS PARTIDA")
        );

        //SHOW ONLY TEXT
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(GuideItem item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getText());
            }
        });

        //HANDLE LIST ITEM CLICKED
        listView.setOnMouseClicked(listEvent -> {
            GuideItem selectedItem = listView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                System.out.println("Item clicado: ID = " + selectedItem.getId() + ", Texto = " + selectedItem.getText());
                handleHowToPlayButtonTab(selectedItem.getId());
            }
        });
        listView.setPrefHeight(585);

        //START GUIDE BUTTON
        Button startGuideButton = new Button("Iniciar Guia");
        startGuideButton.setMinWidth(100);
        startGuideButton.setOnAction(btnEvent -> handleHowToPlayButtonTab(1));


        //VBOX LAYOUT
        VBox layout = new VBox(10, listView, startGuideButton);
        layout.setStyle("-fx-padding: 10px; -fx-alignment: center;");

        //SCENE SETUP
        Scene scene = new Scene(layout, 400, 635);
        guia.setScene(scene);
        guia.show();

        //NÃO DEIXAR 2 INDEXES SEREM ABERTOS AO MESMO TEMPO
        guia.setOnCloseRequest(closeEvenr -> TutorialHandler.getInstance().setTutorialOpen(false));
    }

    public String getTextFromId(int id) {
        switch(id) {
            case 1:
                return "Ao iniciar o jogo, o usuário irá se deparar com a Tela Inicial, a qual conta com o título do aplicativo, além de 5 botões, sendo 3 botões de ação no centro, e 2 botões de informação na parte inferior.";
            case 2:
                return "Ao serem clicados, cada botão realizará uma ação diferente condizente com sua mensagem escrita.\r\n" +
                       "\nOPÇÕES - Leva o jogador à tela de configurações do jogo, onde poderá customizar sua experiência a partir de opções que mudam o comportamento das partidas;\r\n" +
                       "\nJOGAR - Inicia uma partida com as configurações atualmente aplicadas;\r\n" +
                       "\nSAIR - Finaliza o aplicativo;\r\n" +
                       "\nCOMO JOGAR - Leva o jogador à um guia interativo com tudo o que precisa saber para a utilização da aplicação, desde sua primeira inicialização e jogabilidade até a configuração das partidas;\r\n" +
                       "\nFAÇA VOCÊ MESMO - Leva o jogador à um guia interativo específico para a criação e customização de moléculas, para que possam ser utilizadas durante as partidas, juntamente com as configurações necessárias.\r\n";
            case 3:
                return "Ao adentrar o menu de configurações, o usuário será apresentado à diversos indicadores e botões, cada um atribuído a uma diferente característica do jogo, garantindo a maior versatilidade e possibilidade de personalização de partidas possível. \n" +
                       "\nÉ importante ressaltar que todas as configurações retornam ao padrão assim que o jogo é finalizado.";
            case 4:
                return "O “Slider” de volume trata-se de uma barra com valores de 0 a 10, onde o usuário pode aumentar, diminuir ou remover completamente a música do jogo ao clicar com o botão esquerdo do mouse no círculo do centro e arrastá-lo para os lados, ainda com o botão pressionado, soltando-o no ponto desejado.";
            case 5:
                return "A aplicação conta com dois diferentes modos de jogo, sendo eles o modo aleatório (ativado por padrão), e o modo linear, onde o jogador pode escolher qual opção deseja ao marcar ou desmarcar a caixinha ao lado do texto.";
            case 6:
                return "Durante o modo aleatório, as moléculas do banco de dados serão selecionadas de maneira randomizada, assim as rodadas NÃO SEGUIRÃO a ordem no banco de dados.\n" +
                       "\nDurante este modo, uma mesma molécula não pode ser selecionada duas vezes seguidas, mas pode ser reutilizada novamente em uma próxima rodada da mesma partida, tornando este modo infinito até que o jogador deseje finalizar a sessão.";
            case 7:
                return "Durante o modo linear, as moléculas do banco de dados serão selecionadas de maneira contínua, assim as rodadas SEGUIRÃO exatamente a ordem apresentada no banco de dados.\n" + 
                       "\nDurante este modo, devido à linearidade de seleção, uma mesma molécula não aparecerá mais de uma vez durante cada sessão, sendo necessária a finalização da partida para que a mesma apareça novamente. Ao completar todas as moléculas no modo linear, o jogador é redirecionado de volta ao menu principal.";
            case 8:
                return "Por padrão, o banco de dados original do jogo estará sempre selecionado, mas ao clicar no botão de seleção, uma janela de arquivos se abrirá, onde o jogador poderá selecionar um banco de dados customizado em seu computador para a sua sessão de jogo, para confirmar que o arquivo correto foi selecionado, confira o nome do mesmo abaixo do botão.";
            case 9:
                return "Caso deseje retornar ao banco de dados original, basta clicar no botão “Redefinir” ao lado do seletor.\n" +
                       "\nOs bancos de dados devem seguir o padrão apresentado no arquivo “MODELO” que acompanha o jogo durante sua instalação, para mais informações, veja a opção “FAÇA VOCÊ MESMO” do menu inicial.";
            case 10:
                return "O botão “OK” marca a finalização das configurações.\n" +
                       "\n Não é necessário utilizá-lo para sair da tela, sendo possível apenas fechá-la no indicador superior direito “X” e as alterações serão salvas da mesma maneira.\n" +
                       "\nAo clicar no botão, o jogador será direcionado novamente ao menu inicial.";
            case 11:
                return "Ao clicar no botão “JOGAR”, o menu inicial será escondido, dando lugar à uma nova tela onde as partidas ocorrerão.\n" + 
                       "\nNesta tela, existirá o título do aplicativo novamente, assim como o elemento principal do mesmo, o tabuleiro das partidas.";
            case 12:
                return "O tabuleiro das partidas trata-se de um plano de blocos interativos de mesmo tamanho, baseados no clássico “Campo Minado”, contando com 15 linhas e 15 colunas, totalizando 225 blocos ao total.";
            case 13:
                return "Os blocos são a peça principal do tabuleiro, onde possuem 3 estados diferentes, fechados, abertos e vazios, ou abertos e preenchidos.\n" + 
                       "\nInicialmente, todos os blocos se encontrarão fechados, com uma “capa” verde escura escondendo seus conteúdos. Ao serem abertos, os blocos então terão então sua capa removida, revelando assim seu conteúdo, sendo vazios, ou preenchidos por elementos da molécula.";
            case 14:
                return "Ao clicar em qualquer bloco do tabuleiro, este realizará uma ação diferente baseado em seu conteúdo e o botão utilizado durante o clique, a ação também terá características específicas caso seja o primeiro clique, ou caso a rodada já tenha sido finalizada.";
            case 15:
                return "Inicialmente todos os blocos fechados tem a mesma função ao serem clicados, independente do botão do mouse utilizado (esquerdo ou direito), no entanto, o comportamento causado por tal abertura pode ser diferente a partir do conteúdo interno dos blocos.";
            case 16:
                return "Ao abrir blocos preenchidos, o mesmo terá sua capa removida, apresentando assim seu conteúdo para o jogador, sendo este, parte da molécula da rodada.\n" + 
                       "\nNenhum outro bloco é afetado ao abrir um bloco preenchido.";
            case 17:
                return "Ao abrir blocos vazios, os mesmos terão sua capa removida, assim mostrando ao jogador que os mesmos não possuem nenhum conteúdo internamente.\n" + 
                       "\nNo entanto, ao abrir um bloco vazio, todos os outros blocos vazios ao redor serão abertos, este processo é contínuo até que a leva de abertura encontre blocos preenchidos.";
            case 18:
                return "Para que as partidas não sejam todas iguais, o jogo implementa um sistema de posicionamento e rotação dinâmico das moléculas, onde, a cada rodada, uma posição aleatória será definida para a mesma, podendo então aparecer no canto superior, no meio, na parte debaixo, em qualquer posição do tabuleiro.\n" +
                       "\nEm adição, há também o sistema de rotação, o qual fará com que a molécula possa ser transposta para a vertical, garantindo assim mais possibilidades de posicionamento.";
            case 19:
                return "Ao clicar com o botão direito sobre um bloco aberto, o jogador será apresentado à uma nova janela contendo informações acerca do conteúdo do bloco clicado, como dicas e contextos que podem auxiliá-lo a identificar a molécula da rodada.\n" + 
                       "\nCada bloco pode ter até 4 dicas diferentes, mesmo que sejam o mesmo átomo.";
            case 20:
                return "As janelas de dicas NÃO pausam a partida, fazendo assim com que o jogador possa deixá-las abertas durante a rodada.\n" + 
                       "\nAs janelas também não se substituem, desta maneira, o jogador pode deixar mais de uma dica aberta ao mesmo tempo durante a rodada, facilitando assim sua visualização das dicas.";
            case 21:
                return "Ao clicar com o botão esquerdo sobre um bloco já aberto, uma janela de adivinhação será aberta sobre o jogo, onde o jogador poderá tentar adivinhar a molécula da rodada.\n" +
                       "\nO usuário então deverá inserir o nome da molécula, sem precisar se preocupar com espaços ou diferenciação entre letras maiúsculas e minúsculas.";
            case 22:
                return "Caso a resposta inserida pelo jogador não esteja correta, uma nova janela de erro será aberta, indicando que a molécula adivinhada pelo jogador não é a molécula da partida.\n" +
                       "\nAo fechar esta janela, o jogo continuará normalmente. O jogador poderá tentar novamente quantas vezes quiser.";
            case 23:
                return "Caso a resposta inserida pelo jogador esteja correta, uma nova janela de parabenização será aberta, indicando que a molécula adivinhada pelo jogador é a molécula da partida.\n" +
                       "\nAlém disso, um quadro lateral contando com estatísticas de jogo e pontuação do jogador será aberta, juntamente com o botão “Próximo”, que ao ser clicado, levará o jogador para a próxima rodada.";
            case 24:
                return "Ao adivinhar a última molécula de um banco de dados corretamente, uma janela de parabenização será aberta, e então o jogador será levado novamente ao menu inicial, finalizando assim sua sessão de jogo.";
            case 25:
                return "Ao finalizar a sessão, o jogador poderá então escolher jogar novamente, alterar as configurações selecionando outro banco de dados ou mudando o modo de jogo.\n" +
                       "\nAdicionalmente, as estatísticas da partida serão guardadas no arquivo de texto chamado “Detalhes da Partida - <Data e Hora>.txt”, que será criado pelo programa na mesma pasta onde o mesmo se encontra.";
            default:
                return "not found";
        }
    }

    public String getTitleFromId(int searchId) {
        for (GuideItem item : listView.getItems()) {
            if (item.getId() == searchId) {
                String title = item.getText().replaceFirst("^\\s*\\d+([\\.\\d+]*)?\\s*-\\s*", "");
                return title;
            }
        }
        return null;
    }

    public void handleHowToPlayButtonTab(int id) {
        Stage howToPlayStage = new Stage();
        String title = getTitleFromId(id);
        howToPlayStage.setTitle(title);

        String text = getTextFromId(id);

        // Load Image (Replace "image.jpg" with your actual image path)
        Image image = new Image(resourceHandler.getResourcePath("/img/logo_bnw.png"), true);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(400);
        imageView.setFitHeight(300);

        // TextArea (User input)
        TextArea textArea = new TextArea();
        textArea.setPrefSize(350, 300);
        textArea.setText(text);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        
        // "Next" Button
        Button nextButton;
        Button prevButton = null;
        if(id < 25) {
            nextButton = new Button("Próximo");
            prevButton = new Button("Anterior");
            prevButton.setMinWidth(100);
            prevButton.setOnAction(mbnEvent -> {
                if(id < 25) {
                    handleHowToPlayButtonTab(id - 1);
                }
                howToPlayStage.close();
            });

        } else {
            nextButton = new Button("Concluir");
        }
        nextButton.setMinWidth(100);
        nextButton.setOnAction(mbnEvent -> {
            if(id < 25) {
                handleHowToPlayButtonTab(id + 1);
            }
            howToPlayStage.close();
        });

        // Layout
        VBox layout = null;
        if(id < 25) {
            HBox buttons = new HBox(180, prevButton, nextButton);
            layout = new VBox(10, imageView, textArea, buttons);
        } else {
            layout = new VBox(10, imageView, textArea, nextButton);
        }
        layout.setStyle("-fx-padding: 10px; -fx-alignment: center;");
        
        // Scene
        Scene scene = new Scene(layout, 400, 600);
        howToPlayStage.setScene(scene);
        howToPlayStage.show();
    }

    //QUIT BUTTON HANDLER
    public void handleQuitButton(ActionEvent event) {
        System.exit(0);
    }
}