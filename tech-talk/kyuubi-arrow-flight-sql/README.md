# Kyuubi Arrow Flight SQL presentation

Technical slide deck about Kyuubi frontends and the Arrow Flight SQL
integration.

## Layout

```text
presentation/
  kyuubi-flight-sql.md      # Marp source (story + text/diagram pairs)
  kyuubi-flight-sql.pdf     # exported PDF
  speaker-qa.md             # possible listener questions + answers (not in slides)
  generate_diagrams.py      # regenerates concrete technical SVGs
  assets/                   # technical SVG diagrams
```

## Regenerate diagrams / export PDF

```bash
cd presentation
python3 generate_diagrams.py
npx @marp-team/marp-cli kyuubi-flight-sql.md \
  --pdf --allow-local-files -o kyuubi-flight-sql.pdf
```
