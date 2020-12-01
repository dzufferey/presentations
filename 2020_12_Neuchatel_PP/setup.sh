# source this file

if [ -d "venv" ]; then
    source venv/bin/activate
else
    virtualenv venv
    source venv/bin/activate
    pip3 install -r requirements.txt
    jupyter nbextension install --user --py hide_code
    jupyter nbextension enable --user --py hide_code
fi

