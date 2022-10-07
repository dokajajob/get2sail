import traceback

from get2sail_app.models import Location

def clear_locations():
  print("Starting to clear get2sail_app_location table....")
  try:
      Location.objects.all().delete()
      print("get2sail_app_location table clear")
  except Exception:
      print(traceback.print_exc())