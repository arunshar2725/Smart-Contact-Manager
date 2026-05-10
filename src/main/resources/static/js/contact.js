console.log("This is contact page");

const viewContactModal = document.getElementById("view_contact_modal");

// options with default values
const options = {
  placement: "bottom-right",
  backdrop: "dynamic",
  backdropClasses: "bg-gray-900/50 dark:bg-gray-900/80 fixed inset-0 z-40",
  closable: true,
  onHide: () => {
    console.log("modal is hidden");
  },
  onShow: () => {
    console.log("modal is shown");
  },
  onToggle: () => {
    console.log("modal has been toggled");
  },
};

// instance options object
const instanceOptions = {
  id: "view_contact_modal",
  override: true,
};

const contactModal = new Modal(viewContactModal, options, instanceOptions);

function openContactModal() {
  contactModal.show();
}

function closeContactModal() {
  contactModal.hide();
}

async function loadContactData(id) {
  console.log("Fetching data for ID: ", id);
  try {
    const response = await fetch(`http://localhost:8081/api/contacts/${id}`);
    const data = await response.json();
    console.log(data);

    // 1. Text Data Injection
    document.querySelector("#contact_name").textContent = data.name;
    document.querySelector("#contact_email").textContent = data.email;
    document.querySelector("#contact_phone").textContent = data.phoneNumber;

    // Check if address exists, otherwise show 'N/A'
    document.querySelector("#contact_address").textContent = data.address
      ? data.address
      : "Address not provided";

    // Check if description exists
    document.querySelector("#contact_about").textContent = data.description
      ? data.description
      : "No description available.";

    // 2. Handle Profile Picture
    const pictureEl = document.querySelector("#contact_picture");
    if (data.picture) {
      pictureEl.src = data.picture;
    } else {
      // Fallback image if null
      pictureEl.src =
        "https://imgs.search.brave.com/jHDp_R14w-tbRDiYsyiOCGDeCSPE4WqsVfFwiXVDyow/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly90NC5m/dGNkbi5uZXQvanBn/LzExLzY4LzUwLzU3/LzM2MF9GXzExNjg1/MDU3OTRfSUJDRWlh/ZnNJckhGSjA5ZTY1/UDJ2aDUxMTVDMVhJ/N2UuanBn";
    }

    // 3. Handle Favourite Star
    const favEl = document.querySelector("#contact_favourite");
    if (data.favourite) {
      favEl.innerHTML =
        '<span class="bg-yellow-100 text-yellow-800 text-xs font-medium me-2 px-2.5 py-0.5 rounded-full dark:bg-yellow-900 dark:text-yellow-300"><i class="fa-solid fa-star"></i> Favourite</span>';
    } else {
      favEl.innerHTML = ""; // Clear it if not favourite
    }

    // 4. Handle Links (Website & LinkedIn)
    const websiteEl = document.querySelector("#contact_website");
    const linkedinEl = document.querySelector("#contact_linkedin");

    // Reset visibility first
    websiteEl.classList.add("hidden");
    linkedinEl.classList.add("hidden");

    if (data.websiteLink) {
      websiteEl.href = data.websiteLink;
      websiteEl.classList.remove("hidden");
    }

    if (data.linkedinLink) {
      linkedinEl.href = data.linkedinLink;
      linkedinEl.classList.remove("hidden");
    }

    // Finally, show the modal
    openContactModal();
  } catch (error) {
    console.log("Error fetching contact data: ", error);
  }
}
