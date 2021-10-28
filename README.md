# Medcloud's Challenge

As requested, this challenge executes the following:
it first shows a menu with 3 options: 1 - insert patients, 
2 - to generate XML file (this is done after patients are inserted too), 3 - just to exit the program.

When the user inputs the zip code, the program make a request to ViaCEP API, which returns a JSON with
address value from that zip code. If not a valid one, user must entry a new and valid one.
After grabbing the address, it saves the information in the main database.

After storing data in the first database, it reads the XML again and in another database it stores the id, name and timestamp of that patient.
