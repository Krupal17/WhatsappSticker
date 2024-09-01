import pdfplumber
import json

def extract_tables(pdf_path):
    tables = []
    with pdfplumber.open(pdf_path) as pdf:
        for page in pdf.pages:
            table = page.extract_table()
            if table:
                tables.append(table)
    return tables

def convert_tables_to_json(tables):
    json_list = []
    for table in tables:
        if not table:
            continue

        headers = table[0]
        for row in table[1:]:
            record = {headers[i]: row[i] for i in range(len(headers))}
            json_list.append(record)

    return json.dumps(json_list, indent=4)

def extract_tables_as_json(pdf_path):
    tables = extract_tables(pdf_path)
    return convert_tables_to_json(tables)
