#!/bin/bash

# Переименовать файлы с .agents.md на .agent.md
for file in *.agents.md; do
    if [ -f "$file" ]; then
        # Заменяем .agents.md на .agent.md
        newname="${file%.agents.md}.agent.md"
        mv "$file" "$newname"
        echo "Переименован: $file → $newname"
    fi
done

echo "Готово!"
