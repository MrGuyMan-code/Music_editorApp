# 🎵 Cyber BeatBox

Cyber BeatBox is a Java Swing MIDI music editor inspired by classic beat sequencers and piano-roll editors.  
It allows users to create rhythmic and melodic patterns using a customizable beat matrix interface.

The application supports:
- multiple partitures
- multiple instruments
- tempo control
- note stretching
- project save/load
- MIDI playback

Built entirely with:
- Java
- Swing
- Java MIDI API

---

# ✨ Features

## 🎹 Beat Matrix Editor
- Interactive grid-based note editor
- Drag to create longer notes
- Real-time visual note rendering
- Dynamic grid resizing

---

## 🥁 Multiple Instruments
Supports percussion and melodic MIDI instruments.

Examples:
- Bass Drum
- Snare
- Hi-Hat
- Bongos
- Wood Blocks
- Piano
- Synth
- Strings

---

## 🎼 Multiple Partitures
- Create multiple independent musical sections
- Play them sequentially
- Individual instrument control per partiture

---

## ⏱ Tempo Control
- Preset BPM values
- Custom BPM support
- Global tempo synchronization

---

## 💾 Project System
Custom `.cbb` project format:
- save projects
- load projects
- preserves:
  - notes
  - note durations
  - instruments
  - octave shifts
  - beat matrix sizes

---

## 🎨 Steam-Inspired UI
Custom dark theme inspired by Steam:
- custom combo boxes
- custom scroll bars
- styled controls
- colored note visualization

---

# 🛠 Technologies Used

- Java 17+
- Swing
- Java MIDI API (`javax.sound.midi`)
- Object Serialization

---

# 🧠 Architecture

## Main Components

### `MusicEditor`
Main application window and global controls.

### `PartiturePanel`
Represents a musical section containing multiple instruments.

### `InstrumentPanel`
Handles:
- beat matrix
- note editing
- playback
- instrument settings

### `NoteGrid`
Core data structure storing:
- active notes
- continuations
- note lengths

### `MidiPatternPlayer`
Handles MIDI sequence generation and playback.

---

# 🎵 Note System

Each row represents a musical pitch:

```text
SI
LA
SOL
FA
MI
RE
DO
```

Notes can be extended by dragging across beats.

Example:

```text
x > > >
```

represents a sustained note.

---

# 📦 Project Format

Projects are stored using Java object serialization with extension:

```text
.cbb
```

Stored data includes:
- number of partitures
- instruments
- beat counts
- octave shifts
- note states
- continuations

---

# ▶ Running the Project

Compile and run:

```bash
javac music/editor/*.java
java music.editor.MusicEditor
```

---

# 📌 Future Improvements

- piano roll mode
- WAV export
- real MIDI file export
- velocity editing
- quantization
- zoom support
- keyboard shortcuts
- timeline ruler
- undo/redo system

---

# 👨‍💻 Author

Created as a custom Java MIDI sequencer and music editor project.🎵 Cyber BeatBox

Cyber BeatBox is a Java Swing MIDI music editor inspired by classic beat sequencers and piano-roll editors.
It allows users to create rhythmic and melodic patterns using a customizable beat matrix interface.

The application supports:

multiple partitures
multiple instruments
tempo control
note stretching
project save/load
MIDI playback

Built entirely with:

Java
Swing
Java MIDI API
✨ Features
🎹 Beat Matrix Editor
Interactive grid-based note editor
Drag to create longer notes
Real-time visual note rendering
Dynamic grid resizing
🥁 Multiple Instruments

Supports percussion and melodic MIDI instruments.

Examples:

Bass Drum
Snare
Hi-Hat
Bongos
Wood Blocks
Piano
Synth
Strings
🎼 Multiple Partitures
Create multiple independent musical sections
Play them sequentially
Individual instrument control per partiture
⏱ Tempo Control
Preset BPM values
Custom BPM support
Global tempo synchronization
💾 Project System

Custom .cbb project format:

save projects
load projects
preserves:
notes
note durations
instruments
octave shifts
beat matrix sizes
🎨 Steam-Inspired UI

Custom dark theme inspired by Steam:

custom combo boxes
custom scroll bars
styled controls
colored note visualization
🛠 Technologies Used
Java 17+
Swing
Java MIDI API (javax.sound.midi)
Object Serialization
🧠 Architecture
Main Components
MusicEditor

Main application window and global controls.

PartiturePanel

Represents a musical section containing multiple instruments.

InstrumentPanel

Handles:

beat matrix
note editing
playback
instrument settings
NoteGrid

Core data structure storing:

active notes
continuations
note lengths
MidiPatternPlayer

Handles MIDI sequence generation and playback.

🎵 Note System

Each row represents a musical pitch:

SI
LA
SOL
FA
MI
RE
DO

Notes can be extended by dragging across beats.

Example:

x > > >

represents a sustained note.

📦 Project Format

Projects are stored using Java object serialization with extension:

.cbb

Stored data includes:

number of partitures
instruments
beat counts
octave shifts
note states
continuations
▶ Running the Project

Compile and run:

javac music/editor/*.java
java music.editor.MusicEditor
📌 Future Improvements
piano roll mode
WAV export
real MIDI file export
velocity editing
quantization
zoom support
keyboard shortcuts
timeline ruler
undo/redo system
👨‍💻 Author

Created as a custom Java MIDI sequencer and music editor project.
