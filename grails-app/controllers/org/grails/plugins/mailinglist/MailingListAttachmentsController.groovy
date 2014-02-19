package org.grails.plugins.mailinglist




import org.springframework.dao.DataIntegrityViolationException
import org.springframework.web.multipart.commons.CommonsMultipartFile
class MailingListAttachmentsController  {

	
	def addExcept(list) {
		list << 'index' << 'list' << 'show'
	}

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }
	def download(long id)  {
		
		def mailingListAttachmentsInstanceList = MailingListAttachments.get(id)
		
		if ( mailingListAttachmentsInstanceList == null) {
			flash.message = "Document not found."
			redirect (action:'list')
		} else {
			response.setContentType("APPLICATION/OCTET-STREAM")
			response.setHeader("Content-Disposition", "Attachment;Filename=\"${mailingListAttachmentsInstanceList.fullname}\"")

			def outputStream = response.getOutputStream()
			outputStream << mailingListAttachmentsInstanceList.attachment
			outputStream.flush()
			outputStream.close()
		}
	}
	
    def list(Integer max) {
        params.max = Math.min(max ?: 50, 100)
        [mailingListAttachmentsInstanceList: MailingListAttachments.list(params), mailingListAttachmentsInstanceTotal: MailingListAttachments.count()]
    }

    def create() {
        [mailingListAttachmentsInstance: new MailingListAttachments(params)]
    }
	

    def save() {
		
		
		def CommonsMultipartFile uploadedFile = params.attachment
		def contentType = uploadedFile.contentType
		def fileName = uploadedFile.originalFilename
		def size = uploadedFile.size
		def name
		if (fileName.indexOf('.')>-1) { 
			name=fileName.substring(0,fileName.indexOf('.'))
		}else{
			name=fileName
		}	
		params.name=name
		params.contentType=contentType
		params.fullname=fileName
		def found=MailingListAttachments.findByFullname(fileName)
		def mailingListAttachmentsInstance = new MailingListAttachments(params)
		if (found) {
			flash.message = 'Filename '+fileName+' already exists !'
			render(view: "create", model: [mailingListAttachmentsInstance: mailingListAttachmentsInstance])
			return
		}
		if (size<=0){
			flash.message = 'File size equals: '+size+' invalid file size'
			render(view: "create", model: [mailingListAttachmentsInstance: mailingListAttachmentsInstance])
			return
		}
       
        if ((!mailingListAttachmentsInstance.save(flush: true))) {
			render(view: "create", model: [mailingListAttachmentsInstance: mailingListAttachmentsInstance])
            return
        }
				
        flash.message = message(code: 'default.created.message', args: [message(code: 'mailingListAttachments.label', default: 'MailingListAttachments'), mailingListAttachmentsInstance.id])
        redirect(action: "show", id: mailingListAttachmentsInstance.id)
    }

    def show(Long id) {
        def mailingListAttachmentsInstance = MailingListAttachments.get(id)
        if (!mailingListAttachmentsInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'mailingListAttachments.label', default: 'MailingListAttachments'), id])
            redirect(action: "list")
            return
        }

        [mailingListAttachmentsInstance: mailingListAttachmentsInstance]
    }

    def edit(Long id) {
        def mailingListAttachmentsInstance = MailingListAttachments.get(id)
        if (!mailingListAttachmentsInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'mailingListAttachments.label', default: 'MailingListAttachments'), id])
            redirect(action: "list")
            return
        }

        [mailingListAttachmentsInstance: mailingListAttachmentsInstance]
    }

    def update(Long id, Long version) {
        def mailingListAttachmentsInstance = MailingListAttachments.get(id)
        if (!mailingListAttachmentsInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'mailingListAttachments.label', default: 'MailingListAttachments'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (mailingListAttachmentsInstance.version > version) {
                mailingListAttachmentsInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'mailingListAttachments.label', default: 'MailingListAttachments')] as Object[],
                          "Another user has updated this MailingListAttachments while you were editing")
                render(view: "edit", model: [mailingListAttachmentsInstance: mailingListAttachmentsInstance])
                return
            }
        }

        mailingListAttachmentsInstance.properties = params

        if (!mailingListAttachmentsInstance.save(flush: true)) {
            render(view: "edit", model: [mailingListAttachmentsInstance: mailingListAttachmentsInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'mailingListAttachments.label', default: 'MailingListAttachments'), mailingListAttachmentsInstance.id])
        redirect(action: "show", id: mailingListAttachmentsInstance.id)
    }
	
	def del() {
		def mailingListAttachmentsInstance = MailingListAttachments.get(params.id)
		if (!mailingListAttachmentsInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'mailingListAttachments.label', default: 'MailingListAttachments'), params.id])
			redirect(action: "list")
			return
		}

		try {
			mailingListAttachmentsInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'mailingListAttachments.label', default: 'MailingListAttachments'), params.id])
			redirect(action: "list")
		}
		catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'mailingListAttachments.label', default: 'MailingListAttachments'), params.id])
			redirect(action: "list")
		}
	}
	
    def delete(Long id) {
        def mailingListAttachmentsInstance = MailingListAttachments.get(id)
        if (!mailingListAttachmentsInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'mailingListAttachments.label', default: 'MailingListAttachments'), id])
            redirect(action: "list")
            return
        }

        try {
            mailingListAttachmentsInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'mailingListAttachments.label', default: 'MailingListAttachments'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'mailingListAttachments.label', default: 'MailingListAttachments'), id])
            redirect(action: "show", id: id)
        }
    }
}