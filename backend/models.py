from typing import Optional, Dict
from pydantic import BaseModel, Field, EmailStr


class EventOut(BaseModel):
    id: int
    title: str
    category: str
    description: Optional[str] = ""
    event_date: str
    event_time: str
    venue: str
    capacity: int
    fee: int = 0
    icon: Optional[str] = ""
    available_seats: int


# EventDetail uses the same shape as EventOut for this project
class EventDetail(EventOut):
    pass


class RegistrationIn(BaseModel):
    student_name: str
    roll_number: str
    email: str
    phone: Optional[str] = None


class RegistrationOut(BaseModel):
    id: int
    event_id: int
    student_name: str
    registered_at: str


class RegistrationItem(RegistrationOut):
    roll_number: str
    email: str
    phone: Optional[str] = None
    event_title: str


class StatsOut(BaseModel):
    total_events: int
    total_registrations: int
    total_capacity: int
    overall_utilization_pct: float
    upcoming_events_count: int
    events_by_category: Dict[str, int]


# Stretch goal model for admin adding a new event
class EventCreate(BaseModel):
    title: str
    category: str
    description: Optional[str] = ""
    event_date: str
    event_time: str
    venue: str
    capacity: int = Field(gt=0, description="Capacity must be greater than 0")
    fee: int = Field(ge=0, default=0, description="Fee in rupees (0 = free)")
    icon: Optional[str] = "🎉"
