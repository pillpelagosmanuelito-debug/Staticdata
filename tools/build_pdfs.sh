#!/usr/bin/env bash
# Genera los 3 PDF obligatorios (MEMORIA, MANUAL_USUARIO, MANUAL_TECNICO)
# a partir de sus fuentes Markdown en docs/, vía pandoc -> HTML -> wkhtmltopdf.
# Requiere: pandoc, wkhtmltopdf (ambos disponibles en el entorno de build).
set -euo pipefail
cd "$(dirname "$0")/.."

CSS=/tmp/staticdata_pdf.css
cat > "$CSS" << 'CSSEOF'
body { font-family: "DejaVu Sans", sans-serif; font-size: 11pt; color: #10172B; line-height: 1.45; margin: 2cm; }
h1 { color: #C57F1F; border-bottom: 3px solid #E8A23B; padding-bottom: 6px; font-size: 22pt; }
h2 { color: #1D8A7A; margin-top: 22px; font-size: 15pt; border-bottom: 1px solid #E7DCC1; padding-bottom: 3px; }
h3 { color: #33406B; font-size: 12.5pt; }
table { border-collapse: collapse; width: 100%; margin: 10px 0; font-size: 9.5pt; }
th, td { border: 1px solid #C7CEDB; padding: 5px 8px; text-align: left; }
th { background-color: #F4ECD8; }
code, pre { font-family: "DejaVu Sans Mono", monospace; font-size: 9pt; }
pre { background-color: #F4ECD8; padding: 8px; border-radius: 6px; overflow-x: auto; }
blockquote { border-left: 3px solid #E8A23B; margin-left: 0; padding-left: 12px; color: #5B6482; }
.title { font-size: 26pt; color: #10172B; font-weight: bold; }
CSSEOF

build_one () {
  local src="$1"; local title="$2"; local out="$3"
  local html="/tmp/$(basename "$src" .md).html"
  pandoc "$src" -o "$html" --standalone --toc --metadata lang=es -M title="$title" -c "$CSS"
  wkhtmltopdf --enable-local-file-access --margin-top 15 --margin-bottom 15 \
    --margin-left 15 --margin-right 15 "$html" "$out"
  echo "OK -> $out"
}

mkdir -p docs/pdf
build_one docs/MEMORIA_DESCRIPTIVA.md "Memoria Descriptiva — Staticdata" docs/pdf/MEMORIA_DESCRIPTIVA.pdf
build_one docs/MANUAL_USUARIO.md      "Manual de Usuario — Staticdata"   docs/pdf/MANUAL_USUARIO.pdf
build_one docs/MANUAL_TECNICO.md      "Manual Técnico — Staticdata"      docs/pdf/MANUAL_TECNICO.pdf
