import os
import pathlib
import yaml
import sys
sys.path.append(str(pathlib.Path(__file__).resolve().parent.parent))
from backend.database import get_db_connection

CONFIG_PATH = pathlib.Path(__file__).resolve().parent.parent / 'vault_sync.yaml'

def load_config():
    if not CONFIG_PATH.is_file():
        raise FileNotFoundError(f'Vault config not found at {CONFIG_PATH}')
    with open(CONFIG_PATH, 'r', encoding='utf-8') as f:
        return yaml.safe_load(f)

def sync_vault():
    """Synchronize Obsidian vault markdown files with the LMS database.
    Config example::
        vault_path: "D:/o.vault/temp_unzip/PM2-Obsidian-Vault - Copy"
    All markdown files (*.md) under the vault are imported.
    Each immediate sub‑folder becomes a *topic*; markdown files placed
    directly in the vault root are grouped under a special topic named "Root".
    """
    cfg = load_config()
    vault_path = pathlib.Path(cfg.get('vault_path'))
    if not vault_path.is_dir():
        raise NotADirectoryError(f'Vault path not found or not a directory: {vault_path}')

    print('Vault path loaded from config:', vault_path)
    conn = get_db_connection()
    cursor = conn.cursor()

    # Helper to ensure a topic exists and return its id
    def get_or_create_topic(title: str) -> int:
        cursor.execute('SELECT id FROM topics WHERE title = ?', (title,))
        row = cursor.fetchone()
        if row:
            return row['id']
        cursor.execute(
            'INSERT INTO topics (title, category, description, icon) VALUES (?, ?, ?, ?)',
            (title, 'General', '', '')
        )
        topic_id = cursor.lastrowid
        conn.commit()
        print('Created topic', title, 'id', topic_id)
        return topic_id

    # Process markdown files recursively
    md_files = list(vault_path.rglob('*.md'))
    print('Found', len(md_files), 'markdown files in the vault')
    for md_file in md_files:
        # Determine topic based on parent folder
        if md_file.parent == vault_path:
            topic_title = 'Root'
        else:
            # Use the immediate subfolder name as topic
            # Example: vault_path/Concepts/Analytics-Instrumentation.md -> topic "Concepts"
            rel = md_file.parent.relative_to(vault_path)
            # Take only the first part of the relative path as the topic name
            topic_title = rel.parts[0]
        # Normalise title (replace underscores, keep spaces)
        topic_title = topic_title.replace('_', ' ')
        topic_id = get_or_create_topic(topic_title)

        # Extract note title from first heading or filename
        try:
            with open(md_file, 'r', encoding='utf-8') as f:
                first_line = f.readline()
            note_title = first_line.strip().lstrip('#').strip() if first_line.startswith('#') else md_file.stem
        except Exception:
            note_title = md_file.stem

        # Avoid duplicate note entries
        cursor.execute('SELECT id FROM lecture_notes WHERE note_path = ?', (str(md_file),))
        if cursor.fetchone():
            continue
        cursor.execute(
            'INSERT INTO lecture_notes (topic_id, note_path, note_title) VALUES (?, ?, ?)',
            (topic_id, str(md_file), note_title),
        )
        conn.commit()
        print('Inserted note', note_title, 'under topic', topic_title)

    conn.close()
    print('Vault synchronization completed.')

if __name__ == "__main__":
    sync_vault()
